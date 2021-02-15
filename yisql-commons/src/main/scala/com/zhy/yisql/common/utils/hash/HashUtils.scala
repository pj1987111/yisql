package com.zhy.yisql.common.utils.hash

import java.util.UUID

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-15
  *  \* Time: 14:04
  *  \* Description: 
  *  \*/
object HashUtils {
    /**
      * md5 hash
      *
      * @param text
      * @return
      */
    def md5Hash(text: String): String = java.security.MessageDigest.getInstance("MD5").digest(text.getBytes()).map(0xFF & _).map {
        "%02x".format(_)
    }.foldLeft("") {
        _ + _
    }

    /**
      * uuid32 hash
      *
      * @return
      */
    def getUUID32: String = {
        UUID.randomUUID.toString.replace("-", "").toLowerCase
    }
}
