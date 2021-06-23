package org.apache.spark.sql

import org.apache.spark.sql.catalyst.JavaTypeInference
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.execution.command.ExplainCommand
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.sql.types.DataType
import org.apache.spark.status.AppStatusStore
import org.apache.spark.status.api.v1
import org.apache.spark.status.api.v1.StageData
import org.apache.spark.util.Utils

import java.lang.reflect.Type

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-03-03
 *  \* Time: 14:33
 *  \* Description: 
 *  \ */
object SQLUtils {
  def getJavaDataType(tpe: Type): (DataType, Boolean) = {
    JavaTypeInference.inferDataType(tpe)
  }

  def getContextOrSparkClassLoader: ClassLoader = {
    Utils.getContextOrSparkClassLoader
  }

  def localCanonicalHostName: String = {
    Utils.localCanonicalHostName()
  }

  def getAppStatusStore(sparkSession: SparkSession): AppStatusStore = {
    sparkSession.sparkContext.statusStore
  }

  def createStage(stageId: Int): StageData = {
    new v1.StageData(
      v1.StageStatus.PENDING,
      stageId,
      0, 0, 0, 0, 0, 0, 0,
      0L, 0L, None, None, None, None,
      0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L,
      "Unknown",
      None,
      "Unknown",
      null,
      Nil,
      Nil,
      None,
      None,
      Map())
  }

  def createExplainCommand(lg: LogicalPlan, extended: Boolean): ExplainCommand = {
    ExplainCommand(lg, extended = extended)
  }

  def createUserDefinedFunction(f: AnyRef,
                                dataType: DataType,
                                inputTypes: Option[Seq[DataType]]): UserDefinedFunction = {
    UserDefinedFunction(f, dataType, inputTypes)
  }
}
