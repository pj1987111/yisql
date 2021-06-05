package org.apache.spark.sql.types

import scala.collection.mutable.ArrayBuffer

case class WowStructType(list: ArrayBuffer[StructField]) extends DataType {
  override def defaultSize: Int = 0

  override private[spark] def asNullable: DataType = null
}