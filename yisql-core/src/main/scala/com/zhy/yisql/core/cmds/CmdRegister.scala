package com.zhy.yisql.core.cmds

import scala.collection.JavaConverters._

/**
  *  \* Created with IntelliJ IDEA.
  *  \* User: hongyi.zhou
  *  \* Date: 2021-02-14
  *  \* Time: 11:16
  *  \* Description: 
  *  \*/
object CmdRegister {
    private val mapping = new java.util.concurrent.ConcurrentHashMap[String, String]()

    val packagePrefix = "com.zhy.yisql.addon.cmd."

    def easyRegister(name: String) = mapping.put(name, packagePrefix + name)

    def easyRegister2(name: String, value: String) = mapping.put(name, packagePrefix + value)

    def register(name: String, value: String) = mapping.put(name, value)

    def remove(name: String) = mapping.remove(name)

    def getMapping = {
        mapping.asScala
    }


    easyRegister("ShowCommand")
    easyRegister("ShowJobsExt")
    easyRegister("ShowFormatsExt")
    easyRegister2("JDBC", "JDBCCommand")
    easyRegister("EngineResource")
    easyRegister("HDFSCommand")
    easyRegister("NothingET")
    easyRegister("ModelCommand")
    easyRegister("MLSQLEventCommand")
    easyRegister("KafkaCommand")
    easyRegister("DeltaCompactionCommand")
    easyRegister("DeltaCommandWrapper")
    easyRegister("ShowTablesExt")
    register("DTF", "tech.mlsql.ets.tensorflow.DistributedTensorflow")
    easyRegister("PythonCommand")
    easyRegister("SchedulerCommand")
    easyRegister("PluginCommand")
    easyRegister("Ray")
    easyRegister("RunScript")
    easyRegister("PrintCommand")
    easyRegister("IteratorCommand")
    easyRegister("SchemaInferCommand")

    register("IfCommand", "tech.mlsql.ets.ifstmt.IfCommand")
    register("ElifCommand", "tech.mlsql.ets.ifstmt.ElifCommand")
    register("ThenCommand", "tech.mlsql.ets.ifstmt.ThenCommand")
    register("FiCommand", "tech.mlsql.ets.ifstmt.FiCommand")
    register("ElseCommand", "tech.mlsql.ets.ifstmt.ElseCommand")
    easyRegister2("Kill", "KillCommand")
}
