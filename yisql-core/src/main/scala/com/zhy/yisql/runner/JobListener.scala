package com.zhy.yisql.runner

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-18
  *  \* Time: 15:22
  *  \* Description: 
  *  \*/
abstract class JobListener {

    import JobListener._

    def onJobStarted(event: JobStartedEvent): Unit

    def onJobFinished(event: JobFinishedEvent): Unit

}

object JobListener {

    trait JobEvent

    class JobStartedEvent(val groupId:String) extends JobEvent

    class JobFinishedEvent(val groupId:String) extends JobEvent

}