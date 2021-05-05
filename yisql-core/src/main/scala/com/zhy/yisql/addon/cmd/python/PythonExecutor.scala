package com.zhy.yisql.addon.cmd.python

import java.util

import com.zhy.yisql.common.utils.reflect.ScalaMethodMacros
import com.zhy.yisql.core.execute.SQLExecuteContext
import com.zhy.yisql.core.session.SetSession
import org.apache.spark.TaskContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types.{DataType, StructType}
import org.apache.spark.sql.{Dataset, Row, SparkSession, SparkUtils}
import tech.mlsql.arrow.python.ispark.SparkContextImp
import tech.mlsql.arrow.python.runner.{ArrowPythonRunner, ChainedPythonFunctions, PythonConf, PythonFunction}
import tech.mlsql.schema.parser.SparkSimpleSchemaParser

import scala.collection.mutable.ArrayBuffer

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-05-03
  *  \* Time: 20:59
  *  \* Description: 
  *  \*/
object PythonExecutor {
  private def getSchemaAndConf(envSession: SetSession) = {
    def error = {
      throw new RuntimeException(
        """
          |Using `!python conf` to specify the python return value format is required.
          |Do like following:
          |
          |```
          |!python conf "schema=st(field(a,integer),field(b,integer))"
          |```
        """.stripMargin)
    }

    val runnerConf = envSession.fetchPythonRunnerConf match {
      case Some(conf) =>
        val temp = conf.collect().map(f => (f.k, f.v)).toMap
        if (!temp.contains("schema")) {
          error
        }
        temp
      case None => error
    }
    runnerConf
  }

  private def recognizeError(e: Exception) = {
    val buffer = ArrayBuffer[String]()
    //    LogUtils.format_full_exception(buffer, e, true)
    val typeError = buffer.filter(f => f.contains("Previous exception in task: null")).filter(_.contains("org.apache.spark.sql.vectorized.ArrowColumnVector$ArrowVectorAccessor")).size > 0
    if (typeError) {
      throw new RuntimeException(
        """
          |We can not reconstruct data from Python.
          |Try to use !python conf "schema=" change your schema.
        """.stripMargin, e)
    }
    throw e
  }

  def getBinAndRunConf(session: SparkSession) = {
    val context = SQLExecuteContext.getContext()
    val envSession = new SetSession(session, context.owner)
    val envs = Map(
      ScalaMethodMacros.str(PythonConf.PY_EXECUTE_USER) -> context.owner,
      ScalaMethodMacros.str(PythonConf.PYTHON_ENV) -> "export ARROW_PRE_0_15_IPC_FORMAT=1"
    ) ++
      envSession.fetchPythonEnv.get.collect().map { f =>
        if (f.k == ScalaMethodMacros.str(PythonConf.PYTHON_ENV)) {
          (f.k, f.v + " && export ARROW_PRE_0_15_IPC_FORMAT=1")
        } else {
          (f.k, f.v)
        }

      }.toMap

    //    val runnerConf = getSchemaAndConf(envSession) ++ configureLogConf
    val runnerConf = getSchemaAndConf(envSession)
    (envs, runnerConf)
  }

  /**
    * python代码嵌入处理
    * 离线数据
    *
    * @param session
    * @param code        python 代码
    * @param sourceTable 离线源表名
    * @return
    */
  def batchExecute(session: SparkSession, code: String, sourceTable: String) = {
    val df = session.table(sourceTable)
    val binRunConf = getBinAndRunConf(session)

    execute(session, code, df.rdd, df.schema, binRunConf._1, binRunConf._2)
  }

  /**
    * python代码嵌入处理
    * 实时处理回调
    *
    * 由于executor中执行，参数需要外传，因为和外部线程不在同个线程中
    *
    * @param session
    * @param code
    * @param ds
    * @return
    */
  def streamExecute(session: SparkSession, code: String, ds: Dataset[Row],
                    envs: Map[String, String], runnerConf: Map[String, String]) = {
    execute(session, code, ds.rdd, ds.schema, envs, runnerConf)
  }

  private def execute(session: SparkSession, code: String, rdd: RDD[Row], sourceSchema: StructType,
                      envs: Map[String, String], runnerConf: Map[String, String]) = {
    import scala.collection.JavaConverters._


    val targetSchema = runnerConf("schema").trim match {
      case item if item.startsWith("{") =>
        DataType.fromJson(runnerConf("schema")).asInstanceOf[StructType]
      case item if item.startsWith("st") =>
        SparkSimpleSchemaParser.parse(runnerConf("schema")).asInstanceOf[StructType]
      case _ =>
        StructType.fromDDL(runnerConf("schema"))
    }

    val pythonVersion = runnerConf.getOrElse("pythonVersion", "3.6")
    val timezoneID = session.sessionState.conf.sessionLocalTimeZone
    try {
      val data = rdd.mapPartitions { iter =>

        val encoder = RowEncoder.apply(sourceSchema).resolveAndBind()
        val envs4j = new util.HashMap[String, String]()
        envs.foreach(f => envs4j.put(f._1, f._2))

        val batch = new ArrowPythonRunner(
          Seq(ChainedPythonFunctions(Seq(PythonFunction(
            code, envs4j, "python", pythonVersion)))), sourceSchema,
          timezoneID, runnerConf
        )

        val newIter = iter.map { irow =>
          encoder.toRow(irow)
        }
        val commonTaskContext = new SparkContextImp(TaskContext.get(), batch)
        val columnarBatchIter = batch.compute(Iterator(newIter), TaskContext.getPartitionId(), commonTaskContext)
        columnarBatchIter.flatMap { batch =>
          batch.rowIterator.asScala
        }
      }

      SparkUtils.internalCreateDataFrame(session, data, targetSchema, false)
    } catch {
      case e: Exception =>
        recognizeError(e)
    }
  }
}
