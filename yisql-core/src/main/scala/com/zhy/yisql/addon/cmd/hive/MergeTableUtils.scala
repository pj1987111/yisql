package com.zhy.yisql.addon.cmd.hive

import com.google.common.primitives.{Ints, Longs}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.parquet.example.data.Group
import org.apache.parquet.example.data.simple.SimpleGroup
import org.apache.parquet.hadoop.util.HiddenFileFilter
import org.apache.parquet.io.api.Binary
import org.apache.parquet.schema.{GroupType, PrimitiveType}
import org.apache.spark.sql.Row

import java.lang.reflect.Field
import java.sql.Timestamp
import java.util
import java.util.List
import java.util.concurrent.TimeUnit
import scala.collection.JavaConversions._
import scala.collection.mutable.Map

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-13
  *  \* Time: 15:37
  *  \* Description: 
  *  \*/
class MergeTableUtils{}

object MergeTableUtils {
  def getPathFromDirectory(configuration: Configuration, dir: String): List[Path] ={
    val dirPath = new Path(dir)
    val fs = dirPath.getFileSystem(configuration)
    val status = fs.getFileStatus(dirPath)
    val inputFiles = fs.listStatus(status.getPath, HiddenFileFilter.INSTANCE)
    val list = new util.LinkedList[Path]()
    for (file <- inputFiles) {
      list.add(file.getPath)
    }
    list
  }

  def getValue(typeName:String, group:Group, fieldName:String): Any ={
    var value:Any = null
    try {
      if (typeName.equalsIgnoreCase("boolean")) {
        value = group.getBoolean(fieldName, 0)
      } else if (typeName.equalsIgnoreCase("string")) {
        value = group.getString(fieldName, 0)
      } else if (typeName.equalsIgnoreCase("integer")) {
        value = group.getInteger(fieldName, 0)
      } else if (typeName.equalsIgnoreCase("long")) {
        value = group.getLong(fieldName, 0)
      } else if (typeName.equalsIgnoreCase("float")) {
        value = group.getFloat(fieldName, 0)
      } else if (typeName.equalsIgnoreCase("double")) {
        value = group.getDouble(fieldName, 0)
      } else if (typeName.equalsIgnoreCase("timestamp")) {
        value = resolveTimeStamp(group.getInt96(fieldName, 0))
      } else if (typeName.equalsIgnoreCase("array") ||
          typeName.equalsIgnoreCase("map") ||
          typeName.equalsIgnoreCase("struct")) {

        value = resolveGroupValue(group.asInstanceOf[SimpleGroup], fieldName, typeName)
      }
    } catch {
      case _:Exception=>
    }
    value
  }

  def getPrivateField(group: SimpleGroup):Tuple2[GroupType, Array[List[Any]]] ={
    val clazz = classOf[SimpleGroup]
    var schemaField: Field = null
    var dataField: Field = null
    for (field <- clazz.getDeclaredFields) {
      if (field.getName.contains("data")) {
        field.setAccessible(true)
        dataField = field
      }
      if (field.getName.contains("schema")) {
        field.setAccessible(true)
        schemaField = field
      }
    }
    if (schemaField==null || dataField==null) {
      return null
    }
    val schema = schemaField.get(group).asInstanceOf[GroupType]
    val data = dataField.get(group).asInstanceOf[Array[List[Any]]]
    (schema, data)
  }

  def resolveGroupValue(group: SimpleGroup, fieldName:String, typeName:String): Any ={
    val (schema ,data) = getPrivateField(group)
    val index = schema.getFieldIndex(fieldName)
    val value = data(index).get(0)
    if (value.isInstanceOf[SimpleGroup]) {
      if (typeName.equalsIgnoreCase("array")) {
        return resolveValueAsArray(value.asInstanceOf[SimpleGroup])
      } else if (typeName.equalsIgnoreCase("map")) {
        return resolveValueAsMap(value.asInstanceOf[SimpleGroup])
      } else if (typeName.equalsIgnoreCase("struct")) {
        return resolveValueAsStruct(value.asInstanceOf[SimpleGroup])
      }
    }
    null
  }

  def resolveValue(valueType: PrimitiveType.PrimitiveTypeName, value:Any): Any ={
    if (valueType==PrimitiveType.PrimitiveTypeName.BOOLEAN) {
      return value.toString.toBoolean
    } else if (valueType==PrimitiveType.PrimitiveTypeName.INT32) {
      return value.toString.toInt
    } else if (valueType==PrimitiveType.PrimitiveTypeName.INT64 || valueType==PrimitiveType.PrimitiveTypeName.INT96) {
      return value.toString.toLong
    } else if (valueType==PrimitiveType.PrimitiveTypeName.FLOAT) {
      return value.toString.toFloat
    } else if (valueType==PrimitiveType.PrimitiveTypeName.DOUBLE) {
      return value.toString.toDouble
    } else if (valueType==PrimitiveType.PrimitiveTypeName.BINARY) {
      return value.toString
    }
    value
  }

  def resolveValueAsArray(group: SimpleGroup): Any ={
    val (schema, data) = getPrivateField(group)
    val valueList = data(0)
    val array = new Array[Any](valueList.size())
    var index = 0

    val fieldType = schema.getType(0).asInstanceOf[GroupType]
    val valueType = fieldType.getType(0).asInstanceOf[PrimitiveType].getPrimitiveTypeName
    for (value <- valueList) {
      if (value.isInstanceOf[SimpleGroup]) {
        val data1 = getPrivateField(value.asInstanceOf[SimpleGroup])._2
        val rowValue = data1(0).get(0)
        array(index) = resolveValue(valueType, rowValue)
        index += 1
      }
    }
    array
  }

  def resolveValueAsMap(group: SimpleGroup): Any ={
    val (schema, data) = getPrivateField(group)
    val valueList = data(0)
    val fieldType = schema.getType(0).asInstanceOf[GroupType]
    val keyType = fieldType.getType(0).asInstanceOf[PrimitiveType].getPrimitiveTypeName
    val valueType = fieldType.getType(1).asInstanceOf[PrimitiveType].getPrimitiveTypeName

    val map = Map[Any, Any]()
    for (value <- valueList) {
      if (value.isInstanceOf[SimpleGroup]) {
        val data1 = getPrivateField(value.asInstanceOf[SimpleGroup])._2
        val mapKey = resolveValue(keyType, data1(0).get(0))
        val mapValue = resolveValue(valueType, data1(1).get(0))
        map += (mapKey -> mapValue)
      }
    }
    map
  }

  def resolveValueAsStruct(group: SimpleGroup): Any ={
    val (schema, data) = getPrivateField(group)
    val array = new Array[Any](schema.getFields.size())
    for (index <- 0 to schema.getFields.size() - 1) {
      val valueType = schema.getType(index).asInstanceOf[PrimitiveType]
      val value = data(index).get(0)
      array(index) = resolveValue(valueType.getPrimitiveTypeName, value)
    }
    Row.fromSeq(array.toSeq)
  }

  def resolveTimeStamp(value: Binary): Timestamp ={
    if (value.length() != 12) {
      return new Timestamp(0)
    }
    val bytes = value.getBytes
    val timeOfDayNanos = Longs.fromBytes(bytes(7), bytes(6), bytes(5), bytes(4), bytes(3), bytes(2), bytes(1), bytes(0))
    val day = Ints.fromBytes(bytes(11), bytes(10), bytes(9), bytes(8))

    new Timestamp((day-2440588) * TimeUnit.DAYS.toMillis(1) + (timeOfDayNanos / TimeUnit.MILLISECONDS.toNanos(1)))
  }
}

object MergeConfig {

  var skipPathFilter = true

  val sparkMergeRecursive = "spark.parser.merge.recursive"

  val sparkMergeFileNum = "spark.parser.merge.num"

  val sparkMergeFilterSize = "spark.parser.merge.filter.size"

  val sparkInputSplitMaxSize = "spark.parser.input.split.maxsize"

  val sparkInputSplitMinSize = "spark.parser.input.split.minsize"
}