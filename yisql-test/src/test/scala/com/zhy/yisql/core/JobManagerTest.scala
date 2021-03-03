package com.zhy.yisql.core

import org.junit.Test

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-03-02
  *  \* Time: 19:04
  *  \* Description: 
  *  \*/
class JobManagerTest extends BaseTest {
    @Test
    def listJobs(): Unit = {
        jobList(jobListUrl)
    }

    @Test
    def killJobs(): Unit = {
        jobKill(jobKillUrl, Option("9c0e7c64-9a34-4da1-876e-42c330176fad"), None)
    }
}
