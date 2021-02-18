package org.apache.spark.sql.session

import java.util.concurrent.ConcurrentHashMap

import com.zhy.yisql.common.utils.log.Logging
import com.zhy.yisql.runner.JobManager
import org.apache.spark.sql.SparkSession

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-18
  *  \* Time: 14:57
  *  \* Description: 
  *  \*/
class SessionManager(rootSparkSession: SparkSession) extends Logging {

    private[this] val identifierToSession = new ConcurrentHashMap[SessionIdentifier, SQLSession]
    private[this] var shutdown: Boolean = false
    private[this] val opManager = new SQLOperationManager(60)


    def start(): Unit = {
        SparkSessionCacheManager.setSessionManager(this)
        SparkSessionCacheManager.startCacheManager()
    }

    def stop(): Unit = {
        shutdown = true
        SparkSessionCacheManager.get.stop()
    }

    def openSession(
                           username: String,
                           password: String,
                           ipAddress: String,
                           sessionConf: Map[String, String],
                           withImpersonation: Boolean): SessionIdentifier = {

        val session = new SQLSession(
            username,
            password,
            ipAddress,
            withImpersonation,
            this, opManager
        )
        logInfo(s"Opening session for $username")
        session.open(sessionConf)

        identifierToSession.put(SessionIdentifier(username), session)
        SessionIdentifier(username)
    }

    def getSession(sessionIdentifier: SessionIdentifier): SQLSession = {
        synchronized {
            var session = identifierToSession.get(sessionIdentifier)
            if (session == null) {
                openSession(sessionIdentifier.owner, "", "", Map(), true)
            }
            session = identifierToSession.get(sessionIdentifier)
            //to record last visit timestamp
            SparkSessionCacheManager.get.visit(session.getUserName)
            //to record active times
            session.visit()
        }
    }

    def getSessionOption(sessionIdentifier: SessionIdentifier): Option[SQLSession] = {
        val session = getSession(sessionIdentifier)
        if (session == null) None else Some(session)
    }

    def closeSession(sessionIdentifier: SessionIdentifier) {
        val runningJobCnt = JobManager.getJobInfo
                .filter(_._2.owner == sessionIdentifier.owner)
                .size

        if (runningJobCnt == 0) {
            val session = identifierToSession.remove(sessionIdentifier)
            if (session == null) {
                throw new RuntimeException(s"Session $sessionIdentifier does not exist!")
            }
            session.close()
        } else {
            SparkSessionCacheManager.get.visit(sessionIdentifier.owner)
            logInfo(s"Session can't close ,$runningJobCnt jobs are running")
        }
    }

    def getOpenSessionCount: Int = identifierToSession.size
}

