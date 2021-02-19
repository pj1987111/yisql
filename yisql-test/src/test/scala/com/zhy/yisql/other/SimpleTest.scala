package com.zhy.yisql.other

import com.zhy.yisql.common.utils.bean.BeanUtils
import com.zhy.yisql.rest.entity.SQLRunEntity
import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-19
  *  \* Time: 21:08
  *  \* Description: 
  *  \*/
class SimpleTest {
    @Test
    def beanTest(): Unit = {
        val entity = new SQLRunEntity
//        entity.setCallback()
        println(entity)
        val mapV = BeanUtils.getCCParams(entity)
        println(mapV)
    }

    @Test
    def mapTest(): Unit = {
        var params = Map[String, String]()
        params += ("123"->null)
        val v1 = params.getOrElse("123", "123")
        val v2 = params.getOrElse("1234", "123")
        println(1)
    }
}
