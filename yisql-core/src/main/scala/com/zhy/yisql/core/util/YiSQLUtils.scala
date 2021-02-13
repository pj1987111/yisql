package com.zhy.yisql.core.util

import java.util.UUID

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-13
  *  \* Time: 14:50
  *  \* Description: 
  *  \*/
object YiSQLUtils {
    def getUUID32: String = {
        UUID.randomUUID.toString.replace("-", "").toLowerCase
    }
}
