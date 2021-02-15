package com.zhy.yisql.common.utils.log

import org.apache.log4j.LogManager

/**
  * Utility trait for classes that want to log data. Creates a SLF4J logger for the class and allows
  * logging messages at different levels using methods that only evaluate parameters lazily if the
  * log level is enabled.
  */
trait Logging {

    @transient private lazy val log = LogManager.getLogger(this.getClass.getName.stripSuffix("$"))

    // Log methods that take only a String
    protected def logInfo(msg: => String) {
        if (log.isInfoEnabled) log.info(msg)
    }

    protected def logDebug(msg: => String) {
        if (log.isDebugEnabled) log.debug(msg)
    }

    protected def logTrace(msg: => String) {
        if (log.isTraceEnabled) log.trace(msg)
    }

    protected def logWarning(msg: => String) {
        log.warn(msg)
    }

    protected def logError(msg: => String) {
        log.error(msg)
    }

    // Log methods that take Throwables (Exceptions/Errors) too
    protected def logInfo(msg: => String, throwable: Throwable) {
        if (log.isInfoEnabled) log.info(msg, throwable)
    }

    protected def logDebug(msg: => String, throwable: Throwable) {
        if (log.isDebugEnabled) log.debug(msg, throwable)
    }

    protected def logTrace(msg: => String, throwable: Throwable) {
        if (log.isTraceEnabled) log.trace(msg, throwable)
    }

    protected def logWarning(msg: => String, throwable: Throwable) {
        log.warn(msg, throwable)
    }

    protected def logError(msg: => String, throwable: Throwable) {
        log.error(msg, throwable)
    }

    protected def isTraceEnabled(): Boolean = {
        log.isTraceEnabled
    }
}
