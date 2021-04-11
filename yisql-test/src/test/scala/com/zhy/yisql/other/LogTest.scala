package com.zhy.yisql.other

import com.zhy.yisql.common.utils.log.Logging
import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-04-11
  *  \* Time: 12:04
  *  \* Description: 
  *  \*/
class LogTest extends Logging {
  @Test
  def testLog(): Unit = {
    logInfo("hello!")
  }
}
