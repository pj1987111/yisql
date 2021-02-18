package com.zhy.yisql.rest.controller

import org.springframework.web.bind.annotation.{RequestMapping, RestController}

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-17
  *  \* Time: 20:45
  *  \* Description: 
  *  \*/
@RestController
@RequestMapping(value = Array("/sql"))
class SQLScriptController {
    @RequestMapping(value = Array("/test")) def test = "sql test ok"

    @RequestMapping(value = Array("/run")) def run(): Unit = {

    }
}
