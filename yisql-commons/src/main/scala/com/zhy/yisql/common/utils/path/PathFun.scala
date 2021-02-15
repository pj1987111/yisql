package com.zhy.yisql.common.utils.path

import java.io.File

import scala.collection.mutable.ArrayBuffer

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-15
  *  \* Time: 13:44
  *  \* Description: 
  *  \*/
class PathFun(rootPath: String) {

    private val buffer = new ArrayBuffer[String]()
    buffer += rootPath.stripSuffix(PathFun.pathSeparator)

    def add(path: String) = {
        val cleanPath = path.stripPrefix(PathFun.pathSeparator).stripSuffix(PathFun.pathSeparator)
        if (!cleanPath.isEmpty) {
            buffer += cleanPath
        }
        this
    }

    def /(path: String) = {
        add(path)
    }

    def toPath = {
        buffer.mkString(PathFun.pathSeparator)
    }

}

object PathFun {
    val pathSeparator = File.separator
    def apply(rootPath: String): PathFun = new PathFun(rootPath)

    def joinPath(rootPath: String, paths: String*) = {
        val pf = apply(rootPath)
        for (arg <- paths) pf.add(arg)
        pf.toPath
    }
}

