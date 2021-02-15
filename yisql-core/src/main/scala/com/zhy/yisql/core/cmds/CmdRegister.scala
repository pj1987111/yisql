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

    def wow(name: String) = mapping.put(name, ("com.zhy.yisql.cmd." + name))

    def register(name: String, value: String) = mapping.put(name, value)

    def remove(name: String) = mapping.remove(name)

    def getMapping = {
        mapping.asScala
    }


    wow("ShowCommand")
    wow("EngineResource")
    wow("HDFSCommand")
    wow("NothingET")
    wow("ModelCommand")
    wow("MLSQLEventCommand")
    wow("KafkaCommand")
    wow("DeltaCompactionCommand")
    wow("DeltaCommandWrapper")
    wow("ShowTablesExt")
    register("DTF", "tech.mlsql.ets.tensorflow.DistributedTensorflow")
    wow("PythonCommand")
    wow("SchedulerCommand")
    wow("PluginCommand")
    wow("Ray")
    wow("RunScript")
    wow("PrintCommand")
    wow("IteratorCommand")
    wow("SchemaInferCommand")

    register("IfCommand", "tech.mlsql.ets.ifstmt.IfCommand")
    register("ElifCommand", "tech.mlsql.ets.ifstmt.ElifCommand")
    register("ThenCommand", "tech.mlsql.ets.ifstmt.ThenCommand")
    register("FiCommand", "tech.mlsql.ets.ifstmt.FiCommand")
    register("ElseCommand", "tech.mlsql.ets.ifstmt.ElseCommand")
}
