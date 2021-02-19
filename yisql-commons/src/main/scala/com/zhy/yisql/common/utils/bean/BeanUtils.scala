package com.zhy.yisql.common.utils.bean

import scala.collection.JavaConversions._
import org.apache.commons.beanutils.BeanMap

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-19
  *  \* Time: 20:28
  *  \* Description: 
  *  \*/
object BeanUtils {
//    def getCCParamsRecur(cc: Product): Map[String, Any] = {
//        val values = cc.productIterator
//        cc.getClass.getDeclaredFields.map {
//            _.getName -> (values.next() match {
//                case p: Product if p.productArity > 0 => getCCParamsRecur(p)
//                case x => x
//            })
//        }.toMap
//    }

//    def getCCParams(cc: AnyRef) = {
//        cc.getClass.getDeclaredFields.foldLeft(Map.empty[String, String]) { (a, f) =>
//            f.setAccessible(true)
//            val valV = f.get(cc)
//            a + (f.getName -> (if (f.get(cc) != null) f.get(cc).toString else null))
//        }
//    }

    def getCCParams(cc: AnyRef) = {
        var valMap = Map[String, String]()
        if (cc != null) {
            val beanMap = new BeanMap(cc)
            for (key <- beanMap.keySet()) {
                val keyV = beanMap.get(key)
                if (keyV != null && !key.equals("class"))
                    valMap += (key + "" -> keyV.toString)
            }
        }
        valMap
    }
}
