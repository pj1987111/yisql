package com.zhy.yisql.rest

import com.zhy.yisql.netty.core.WebServer
import com.zhy.yisql.netty.rest.controller.ExceptionController
import com.zhy.yisql.netty.rest.interceptor.CorsInterceptor

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-03-24
 *  \* Time: 13:27
 *  \* Description: 
 *  \ */
object RestServer {
  def main(args: Array[String]): Unit = {
    val port: Int = args(0).toInt
    // 全局异常处理
    WebServer.setExceptionHandler(new ExceptionController)

    // 设置监听端口号
    val server = new WebServer(port)

    // 设置Http最大内容长度（默认 为10M）
    server.setMaxContentLength(1024 * 1024 * 50)

    // 设置Controller所在包
    server.setControllerBasePackage("com.zhy.yisql.rest.controller")

    // 添加拦截器，按照添加的顺序执行。
    // 跨域拦截器
    server.addInterceptor(new CorsInterceptor, "/不用拦截的url")

    try
      server.start()
    catch {
      case e: InterruptedException =>
        e.printStackTrace()
    }
  }
}
