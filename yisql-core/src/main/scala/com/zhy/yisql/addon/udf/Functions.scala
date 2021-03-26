package com.zhy.yisql.addon.udf

import java.util.UUID

import org.apache.spark.sql.UDFRegistration

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-18
  *  \* Time: 16:16
  *  \* Description: 
  *  \*/
object Functions {
    def parse(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("parse", (co: String) => {
            val parseMethod = Class.forName("org.ansj.splitWord.analysis.NlpAnalysis").getMethod("parse", classOf[String])
            val tmp = parseMethod.invoke(null, co)
            val terms = tmp.getClass.getMethod("getTerms").invoke(tmp).asInstanceOf[java.util.List[Any]]
            terms.map(f => f.asInstanceOf[ {def getName: String}].getName).toArray
        })
    }

    def mkString(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("mkString", (sep: String, co: mutable.WrappedArray[String]) => {
            co.mkString(sep)
        })
    }

    def uuid(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("uuid", () => {
            UUID.randomUUID().toString.replace("-", "")
        })
    }

    def sleep(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("sleep", (sleep: Long) => {
            Thread.sleep(sleep)
            ""
        })
    }



    def array_intersect(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("array_intersect", (vec1: Seq[String], vec2: Seq[String]) => {
            vec1.intersect(vec2)
        })
    }


    def array_index(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("array_index", (vec1: Seq[String], word: Any) => {
            vec1.indexOf(word)
        })
    }

    def array_slice(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("array_slice", (vec1: Seq[String], from: Int, to: Int) => {
            if (to == -1) {
                vec1.slice(from, vec1.length)
            } else {
                vec1.slice(from, to)
            }
        })
    }

    def array_number_concat(uDFRegistration: UDFRegistration) = {

        uDFRegistration.register("array_number_concat", (a: Seq[Seq[Number]]) => {
            a.flatMap(f => f).map(f => f.doubleValue())
        })
    }

    def array_concat(uDFRegistration: UDFRegistration) = {

        uDFRegistration.register("array_concat", (a: Seq[Seq[String]]) => {
            a.flatMap(f => f)
        })
    }

    def array_number_to_string(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("array_number_to_string", (a: Seq[Number]) => {
            a.map(f => f.toString)
        })
    }

    def array_string_to_double(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("array_string_to_double", (a: Seq[String]) => {
            a.map(f => f.toDouble)
        })
    }

    def map_value_int_to_double(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("map_value_int_to_double", (a: Map[String, Int]) => {
            a.map(f => (f._1, f._2.toDouble))
        })
    }

    def array_string_to_float(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("array_string_to_float", (a: Seq[String]) => {
            a.map(f => f.toFloat)
        })
    }

    def array_string_to_int(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("array_string_to_int", (a: Seq[String]) => {
            a.map(f => f.toInt)
        })
    }

    def toArrayDouble(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("to_array_double", (seq: Seq[Object]) => {
            seq.map(a => a.toString.toDouble)
        })
    }

    def ngram(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("ngram", (words: Seq[String], n: Int) => {
            words.iterator.sliding(n).withPartial(false).map(_.mkString(" ")).toSeq
        })
    }

    def decodeKafka(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("decodeKafka", (item: Array[Byte]) => {
            new String(item, "utf-8")
        })
    }

    def paddingIntArray(uDFRegistration: UDFRegistration) = {
        uDFRegistration.register("padding_int_array", (seq: Seq[Int], length: Int, default: Int) => {
            if (seq.length > length) {
                seq.slice(0, length)
            } else {
                seq ++ Seq.fill(length - seq.length)(default)
            }
        })
    }

}
