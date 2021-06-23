package com.zhy.yisql.core.cmds

import scala.collection.JavaConverters._
import scala.collection.concurrent

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-02-14
 *  \* Time: 11:16
 *  \* Description: 
 *  \ */
object CmdRegister {
  private val mapping = new java.util.concurrent.ConcurrentHashMap[String, String]()

  val packagePrefix = "com.zhy.yisql.addon.cmd."

  def easyRegister(name: String): String = mapping.put(name, packagePrefix + name)

  def easyRegister2(name: String, value: String): String = mapping.put(name, packagePrefix + value)

  def register(name: String, value: String): String = mapping.put(name, value)

  def remove(name: String): String = mapping.remove(name)

  def getMapping: concurrent.Map[String, String] = {
    mapping.asScala
  }


  easyRegister("ShowCommand")
  easyRegister("ShowJobsExt")
  easyRegister("ShowFormatsExt")
  easyRegister("ShowSchemaExt")
  easyRegister2("JDBC", "JDBCCommand")
  easyRegister("HiveCommand")
  easyRegister("EngineResource")
  easyRegister("HDFSCommand")
  easyRegister("NothingET")
  easyRegister("ModelCommand")
  easyRegister("EventCommand")
  easyRegister("KafkaCommand")
  easyRegister("DeltaCompactionCommand")
  easyRegister("DeltaCommandWrapper")
  easyRegister("ShowTablesExt")
  register("DTF", "DistributedTensorflow")
  easyRegister("PythonCommand")
  easyRegister("SchedulerCommand")
  easyRegister("PluginCommand")
  easyRegister("Ray")
  easyRegister("RunScript")
  easyRegister("PrintCommand")
  easyRegister("IteratorCommand")
  easyRegister("SchemaInferCommand")

  easyRegister2("Kill", "KillCommand")
}
