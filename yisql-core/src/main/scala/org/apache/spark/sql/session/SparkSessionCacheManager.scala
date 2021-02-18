package org.apache.spark.sql.session

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{ConcurrentHashMap, Executors, TimeUnit}

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.zhy.yisql.common.utils.log.Logging
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConverters._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-18
  *  \* Time: 15:00
  *  \* Description: 
  *  \*/
class SparkSessionCacheManager extends Logging {
    private val cacheManager =
        Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder()
                    .setDaemon(true).setNameFormat(getClass.getSimpleName + "-%d").build())

    private[this] val userToSparkSession =
        new ConcurrentHashMap[String, (SparkSession, AtomicInteger)]

    private[this] val userLatestVisit = new ConcurrentHashMap[String, Long]


    def set(user: String, sparkSession: SparkSession): Unit = {
        userToSparkSession.put(user, (sparkSession, new AtomicInteger(1)))
        userLatestVisit.put(user, System.currentTimeMillis())
    }

    def getAndIncrease(user: String): Option[SparkSession] = {
        Some(userToSparkSession.get(user)) match {
            case Some((ss, times)) if !ss.sparkContext.isStopped =>
                logInfo(s"SparkSession for [$user] is reused for ${times.incrementAndGet()} times.")
                Some(ss)
            case _ =>
                logInfo(s"SparkSession for [$user] isn't cached, will create a new one.")
                None
        }
    }


    def visit(user: String): Unit = {
        userLatestVisit.put(user, System.currentTimeMillis())
    }

    def closeSession(user: String, forceClose: Boolean = false): Unit = {
        // if the session have stream job, we should not close the session.
        val spark = SparkSessionCacheManager.getSessionManager.getSession(SessionIdentifier(user)).sparkSession
        if (spark.streams.active.length == 0 || forceClose) {
            SparkSessionCacheManager.getSessionManager.closeSession(SessionIdentifier(user))
            userToSparkSession.remove(user)
        } else {
            logWarning(s"SparkSession for user [$user] have active stream jobs, we will not remove it.")
        }
    }

    private[this] val sessionCleaner = new Runnable {
        override def run(): Unit = {
            userToSparkSession.asScala.foreach {
                case (user, (session, _)) if userLatestVisit.get(user) + SparkSessionCacheManager.getExpireTimeout <= System.currentTimeMillis() =>
                    logInfo(s"Stopping idle SparkSession for user [$user].")
                    closeSession(user)
                case _ =>
            }
        }
    }

    def users = {
        userToSparkSession.asScala.map(_._1).toList
    }

    /**
      * Periodically close idle SparkSessions
      */
    def start(): Unit = {
        // at least 1 minutes
        logInfo(s"Scheduling SparkSession cache cleaning every 60 seconds")
        cacheManager.scheduleAtFixedRate(sessionCleaner, 60, 60, TimeUnit.SECONDS)
    }

    def stop(): Unit = {
        logInfo("Stopping SparkSession Cache Manager")
        cacheManager.shutdown()
        userToSparkSession.asScala.values.foreach { kv => kv._1.stop() }
        userToSparkSession.clear()
    }
}

object SparkSessionCacheManager {

    type Usage = AtomicInteger

    private[this] var sparkSessionCacheManager: SparkSessionCacheManager = _

    private[this] var sessionManager: SessionManager = _

    private[this] val EXPIRE_SMALL_TIMEOUT = 1 * 60 * 60 * 1000L
    private[this] var expireTimeout = EXPIRE_SMALL_TIMEOUT

    def startCacheManager(): Unit = {
        sparkSessionCacheManager = new SparkSessionCacheManager()
        sparkSessionCacheManager.start()
    }

    def setSessionManager(manager: SessionManager): Unit = {
        sessionManager = manager
    }

    def getSessionManager: SessionManager = sessionManager

    def getSessionManagerOption: Option[SessionManager] = if (sessionManager == null) None else Some(sessionManager)


    def setExpireTimeout(expire: Long): String = {
        if (expire > EXPIRE_SMALL_TIMEOUT) {
            expireTimeout = expire
            s"set session expire success $expire"
        } else {
            s"session expire must bigger than $EXPIRE_SMALL_TIMEOUT ,current is $expire"
        }
    }

    def getExpireTimeout: Long = expireTimeout

    def get: SparkSessionCacheManager = sparkSessionCacheManager
}
