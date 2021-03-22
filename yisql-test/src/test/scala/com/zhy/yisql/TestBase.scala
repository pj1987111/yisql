package com.zhy.yisql

import java.io.DataOutputStream
import java.net.Socket

import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-20
  *  \* Time: 12:08
  *  \* Description: 
  *  \*/
class TestBase {
  @Test
  def testSocket(): Unit = {
    val socket = new Socket("127.0.0.1", 6049)
    val dOut = new DataOutputStream(socket.getOutputStream)
    for(a <- 1 to 20) {
      println(a)
      dOut.write("123456\n".getBytes())
      dOut.flush()
    }
//    dOut.close()
  }
}
