package com.zhy.yisql.common.utils.reflect

import scala.util.Try

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-17
  *  \* Time: 23:10
  *  \* Description: 
  *  \*/
object ClassLoaderTool {
    def getDefaultLoader: ClassLoader = getClass.getClassLoader

    def getContextOrDefaultLoader: ClassLoader =
        Option(Thread.currentThread().getContextClassLoader).getOrElse(getDefaultLoader)

    /** Determines whether the provided class is loadable in the current thread. */
    def classIsLoadable(clazz: String): Boolean = {
        // scalastyle:off classforname
        Try { Class.forName(clazz, false, getContextOrDefaultLoader) }.isSuccess
        // scalastyle:on classforname
    }

    // scalastyle:off classforname
    /** Preferred alternative to Class.forName(className) */
    def classForName(className: String): Class[_] = {
        Class.forName(className, true, getContextOrDefaultLoader)
        // scalastyle:on classforname
    }

    /**
      * Run a segment of code using a different context class loader in the current thread
      */
    def withContextClassLoader[T](ctxClassLoader: ClassLoader)(fn: => T): T = {
        val oldClassLoader = Thread.currentThread().getContextClassLoader()
        try {
            Thread.currentThread().setContextClassLoader(ctxClassLoader)
            fn
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader)
        }
    }

}

