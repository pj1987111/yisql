package org.apache.spark.utils.json

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-09
  *  \* Time: 19:12
  *  \* Description: 
  *  \*/
object JsonUtils {
    /** Used to convert between classes and JSON. */
    val mapper = new ObjectMapper with ScalaObjectMapper
    mapper.setSerializationInclusion(Include.NON_NULL)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.registerModule(DefaultScalaModule)

    def toJson[T: Manifest](obj: T): String = {
        mapper.writeValueAsString(obj)
    }

    def toPrettyJson[T: Manifest](obj: T): String = {
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
    }

    def fromJson[T: Manifest](json: String): T = {
        mapper.readValue[T](json)
    }
}
