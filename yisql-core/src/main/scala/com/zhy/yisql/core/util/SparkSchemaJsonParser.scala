package com.zhy.yisql.core.util

import org.apache.spark.sql.catalyst.expressions.JsonToStructs
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Column, DataFrame, functions => F}

import scala.collection.mutable.ArrayBuffer

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-15
  *  \* Time: 22:29
  *  \* Description: 
  * 类json表达式转SPARK StructedField
  *
  * 默认支持最外层为数组
  *
  * 非嵌套
  * st(field(id,string),field(name,string))
  *
  * 嵌套
  * st(field(table,string),field(data,st(field(id,string))))
  *  \*/
object SparkSchemaJsonParser {
    private def findInputInArrayBracket(input: String) = {
        val max = input.length - 1
        val rest = ArrayBuffer[Char]()
        var firstS = false
        var fBracketCount = 0
        (0 until max).foreach { i =>
            input(i) match {
                case '(' =>
                    if (firstS) {
                        rest += input(i)
                        fBracketCount += 1
                    } else {
                        firstS = true
                    }
                case ')' => fBracketCount -= 1
                    if (fBracketCount < 0) {
                        firstS = false
                    } else {
                        rest += input(i)
                    }
                case _ =>
                    if (firstS) {
                        rest += input(i)
                    }

            }
        }
        rest.mkString("")
    }

    private def findKeyAndValue(input: String) = {
        val max = input.length - 1
        var fBracketCount = 0
        val positionArr = new ArrayBuffer[Integer]()

        (0 until max).foreach { i =>
            input(i) match {
                case '(' =>
                    fBracketCount += 1
                case ')' =>
                    fBracketCount -= 1
                case ',' =>
                    if (fBracketCount == 0) {
                        positionArr += i
                    }
                case _ =>
            }
        }
        if (positionArr.size == 2)
            (input.substring(0, positionArr(0)), input.substring(positionArr(0) + 1, positionArr(1)), input.substring(positionArr(1) + 1))
        else
            (input.substring(0, positionArr(0)), input.substring(positionArr(0) + 1), "")
    }

    //array(array(map(string,string)))
    private def toSparkType(dt: String): DataType = {
        dt match {
            case "boolean" => BooleanType
            case "byte" => ByteType
            case "short" => ShortType
            case "integer" => IntegerType
            case "date" => DateType
            case "long" => LongType
            case "float" => FloatType
            case "double" => DoubleType
            case "decimal" => DoubleType
            case "binary" => BinaryType
            case "string" => StringType
            case c: String if c.startsWith("array") =>
                ArrayType(toSparkType(findInputInArrayBracket(c)))
            case c: String if c.startsWith("map") =>
                //map(map(string,string),string)
                val (key, value, alias) = findKeyAndValue(findInputInArrayBracket(c))
                MapType(toSparkType(key), toSparkType(value))

            case _ => throw new RuntimeException(s"$dt is not found spark type")

        }
    }

    private def getAlias(meta: Metadata) = {
        var res = ""
        try {
            res = meta.getString("alias")
        } catch {
            case _: Exception =>
        }
        res
    }

    /**
      * 嵌套字段解析
      *
      * @param columns
      * @param parentName
      * @param sourceSchema
      */
    def dfs(columns: ArrayBuffer[Column], parentName: String, sourceSchema: StructType): Unit = {
        for (schema <- sourceSchema) {
            val curName = s"$parentName.${schema.name}"
            schema.dataType match {
                case dType: StructType => dfs(columns, curName, dType)
                case _ => {
                    val aliasName = getAlias(schema.metadata)
                    if (aliasName.isEmpty)
                        columns.append(F.expr(s"$curName"))
                    else
                        columns.append(F.expr(s"$curName as $aliasName"))
                }
            }
        }
    }

    /**
      *
      * @param oldDf       one col named "value"
      * @param str
      * @param containsRaw 返回是否包含原始value字段
      *
      *                    注意 如果有两个嵌套同名列 必须用别名区分
      */
    def parseDataFrame(oldDf: DataFrame, str: String, containsRaw: Boolean = false) = {
        //        var newDf = oldDf.select(F.explode(oldDf("value"))).toDF("value")
        var newDf = oldDf
        val sourceSchema = parse(str)
        //结合explode应对外层数组情况
        val arraySourceSchema = ArrayType(sourceSchema)
        newDf = newDf.select(new Column(JsonToStructs(arraySourceSchema, Map(), F.col("value").expr, None)).as("data"), F.col("value"))
        //解决原始数据为数组情况
        newDf = newDf.select(F.explode(newDf("data")), F.col("value")).toDF("data", "value")
        val columns = new ArrayBuffer[Column]()
        dfs(columns, "data", sourceSchema)
        if (containsRaw)
            columns.append(F.expr("value as raw"))
        newDf = newDf.select(columns: _*)
        newDf
    }

    def parse(str: String) = {
        toInnerStructType(str) match {
            case s: InnerStructType => toStructType(s)
            //                    case s: DataType =>
            //                        val buf = new ArrayBuffer[StructField]()
            //                        buf += StructField("value", s)
            //                        toStructType(InnerStructType(buf)).head.dataType
        }
    }

    def parseRaw(str: String): DataType = {
        toInnerStructType(str) match {
            case s: InnerStructType => toStructType(s)
            case s: DataType => StructType(Seq(StructField("value", s)))
        }
    }

    private def toStructType(innerStructType: InnerStructType): StructType = {
        StructType(innerStructType.list.map { field =>
            field.dataType match {
                case structType: InnerStructType =>
                    StructField(field.name, toStructType(structType))
                case _ =>
                    field
            }
        })
    }

    //st(field(name,string),field(name1,st(field(name2,array(string)))))
    private def toInnerStructType(dt: String, st: InnerStructType = InnerStructType(ArrayBuffer[StructField]())): DataType = {
        def startsWith(c: String, token: String) = {
            c.startsWith(token) || c.startsWith(s"${token} ") || c.startsWith(s"${token}(")
        }

        dt match {
            case "boolean" => BooleanType
            case "byte" => ByteType
            case "short" => ShortType
            case "integer" => IntegerType
            case "date" => DateType
            case "long" => LongType
            case "float" => FloatType
            case "double" => DoubleType
            case "decimal" => DoubleType
            case "binary" => BinaryType
            case "string" => StringType

            case c: String if startsWith(c, "array") =>
                ArrayType(toInnerStructType(findInputInArrayBracket(c), st))
            case c: String if startsWith(c, "map") =>
                //map(map(string,string),string)
                val (key, value, alias) = findKeyAndValue(findInputInArrayBracket(c))
                MapType(toInnerStructType(key, st), toInnerStructType(value, st))

            case c: String if startsWith(c, "st") =>
                val value = findInputInArrayBracket(c)
                val wst = InnerStructType(ArrayBuffer[StructField]())
                toInnerStructType(value, wst)


            case c: String if startsWith(c, "field") =>
                val filedStrArray = ArrayBuffer[String]()
                findFieldArray(c, filedStrArray)

                filedStrArray.foreach { fs =>
                    val (key, value, alias) = findKeyAndValue(findInputInArrayBracket(fs))
                    st.list += StructField(key, toInnerStructType(value, st),
                        metadata = new MetadataBuilder().putString("alias", alias).build())
                }
                st

            case _ => throw new RuntimeException("dt is not found spark schema")
        }
    }

    private def findFieldArray(input: String, fields: ArrayBuffer[String]): Unit = {
        val max = input.length
        var fBracketCount = 0
        var position = 0
        var stop = false
        (0 until max).foreach { i =>
            if (!stop) {
                input(i) match {
                    case '(' =>
                        fBracketCount += 1
                    case ')' =>
                        fBracketCount -= 1
                        if (i == max - 1 && fBracketCount == 0) {
                            fields += input.substring(0, max)
                        }
                    case ',' =>
                        if (fBracketCount == 0) {
                            position = i
                            fields += input.substring(0, position)
                            findFieldArray(input.substring(position + 1), fields)
                            stop = true
                        }
                    case _ =>
                        if (i == max - 1 && fBracketCount == 0) {
                            fields += input.substring(0, max + 1)
                        }
                }
            }

        }
    }

    def serializeSchema(sourceSchema: StructType): String = {
        val columns = new ArrayBuffer[String]()
        for (schema <- sourceSchema) {
            schema.dataType match {
                case dType: StructType => {
                    val innerV = serializeSchema(dType)
                    columns.append(s"field(${schema.name},$innerV)")
                }
                case _ => {
                    columns.append(s"field(${schema.name},string)")
                }
            }
        }
        s"st(${columns.mkString(",")})"
    }
}

case class SchemaField(name: String, value: Any)

case class InnerStructType(list: ArrayBuffer[StructField]) extends DataType {
    override def defaultSize: Int = 0

    override def asNullable: DataType = null
}
