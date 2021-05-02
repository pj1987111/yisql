package com.zhy.yisql.addon.cmd

import java.util

import com.zhy.yisql.common.utils.json.JSONTool
import com.zhy.yisql.common.utils.reflect.ScalaMethodMacros
import com.zhy.yisql.core.cmds.SQLCmd
import com.zhy.yisql.core.execute.SQLExecuteContext
import com.zhy.yisql.core.session.SetSession
import org.apache.spark.TaskContext
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types.{DataType, StructType}
import org.apache.spark.sql.{DataFrame, SparkSession, SparkUtils}
import tech.mlsql.arrow.python.ispark.SparkContextImp
import tech.mlsql.arrow.python.runner.{ArrowPythonRunner, ChainedPythonFunctions, PythonConf, PythonFunction}
import tech.mlsql.schema.parser.SparkSimpleSchemaParser

import scala.collection.mutable.ArrayBuffer

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-28
  *  \* Time: 19:52
  *  \* Description: 
  *  \*/
class PythonCommand extends SQLCmd {
  override def run(spark: SparkSession, path: String, params: Map[String, String]): DataFrame = {
    val context = SQLExecuteContext.getContext()
    //user级别session
    val envSession = new SetSession(spark, context.owner)


    val command = JSONTool.parseJson[List[String]](params("parameters")).toArray

    val newdf = command match {

      case Array("env", kv) => // !python env a=b
        val Array(k, v) = kv.split("=", 2)
        envSession.set(k, v, Map(SetSession.__MLSQL_CL__ -> SetSession.PYTHON_ENV_CL))
        envSession.fetchPythonEnv.get.toDF()

      case Array("conf", kv) => // !python conf a=b
        val Array(k, v) = kv.split("=", 2)
        envSession.set(k, v, Map(SetSession.__MLSQL_CL__ -> SetSession.PYTHON_RUNNER_CONF_CL))
        envSession.fetchPythonRunnerConf.get.toDF()

      case Array("on", tableName, code) =>
        distribute_execute(spark, code, tableName)

      case Array("on", tableName, code, "named", targetTable) =>
        val resDf = distribute_execute(spark, code, tableName)
        resDf.createOrReplaceTempView(targetTable)
        resDf

    }
    newdf
  }

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

  private def distribute_execute(session: SparkSession, code: String, sourceTable: String) = {
    import scala.collection.JavaConverters._
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
    val df = session.table(sourceTable)
    val sourceSchema = df.schema
    try {
      val data = df.rdd.mapPartitions { iter =>

        val enconder = RowEncoder.apply(sourceSchema).resolveAndBind()
        val envs4j = new util.HashMap[String, String]()
        envs.foreach(f => envs4j.put(f._1, f._2))

        val batch = new ArrowPythonRunner(
          Seq(ChainedPythonFunctions(Seq(PythonFunction(
            code, envs4j, "python", pythonVersion)))), sourceSchema,
          timezoneID, runnerConf
        )

        val newIter = iter.map { irow =>
          enconder.toRow(irow)
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
