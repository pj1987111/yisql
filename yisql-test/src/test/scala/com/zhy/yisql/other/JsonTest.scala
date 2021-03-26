package com.zhy.yisql.other

import com.zhy.yisql.common.utils.bean.BeanUtils
import com.zhy.yisql.common.utils.json.JSONTool
import org.junit.Test
import com.zhy.yisql.rest.entity.SQLRunEntity

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-24
  *  \* Time: 17:46
  *  \* Description: 
  *  \*/
class JsonTest {
  @Test
  def testJsonSer(): Unit = {
    val jsonStr = "{\"defaultPathPrefix\":\"/user/datacompute/export\",\"owner\":\"testzhy\",\"sql\":\"\\n!show jobs\\n        \"}"
    val obj = JSONTool.parseJson[SQLRunEntity](jsonStr).defaults
    val params: Map[String, String] = BeanUtils.getCCParams(obj)
    println(obj)
    println(params)
  }
}
