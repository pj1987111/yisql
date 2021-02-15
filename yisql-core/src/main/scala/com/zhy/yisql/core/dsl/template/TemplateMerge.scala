package com.zhy.yisql.core.dsl.template

import com.zhy.yisql.common.utils.render.RenderEngine
import org.joda.time.DateTime

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-05
  *  \* Time: 12:56
  *  \* Description: 
  *  \*/
object TemplateMerge {
    def merge(sql: String, root: Map[String, String]): String = {

        val dformat = "yyyy-MM-dd"
        //2018-03-24
        val predified_variables = Map[String, String](
            "yesterday" -> DateTime.now().minusDays(1).toString(dformat),
            "today" -> DateTime.now().toString(dformat),
            "tomorrow" -> DateTime.now().plusDays(1).toString(dformat),
            "theDayBeforeYesterday" -> DateTime.now().minusDays(2).toString(dformat)
        )
        val newRoot = Map("date" -> new DateTime()) ++ predified_variables ++ root
        val wow = RenderEngine.namedEvaluate(sql, newRoot)
        wow
    }
}
