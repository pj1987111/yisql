package com.zhy.yisql.core.datasource.datalake

import com.zhy.yisql.common.utils.path.PathFun
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.SparkSession

class DataLake(sparkSession: SparkSession) {

    val BUILD_IN_DB_PREFIX = "__instances__"

    def appName = sparkSession.sparkContext.appName

    def buildInDBs = Set("__yisql__", "__tmp__")

    def isEnable = sparkSession.sessionState.conf.contains(DataLake.SPARK_DL_PATH)

    def overwriteHive = sparkSession.sessionState.conf.getConfString(DataLake.DELTA_LAKE_OVERWRITE_HIVE, "false").toBoolean

    def value = {
        sparkSession.sessionState.conf.getConfString(DataLake.SPARK_DL_PATH)
    }

    def dbAndtableToPath(db: String, table: String) = {
        if (buildInDBs.contains(db)) {
            PathFun(value).add(BUILD_IN_DB_PREFIX).add(appName).add(db).add(table).toPath
        } else {
            PathFun(value).add(db).add(table).toPath
        }

    }

    def identifyToPath(dbAndTable: String) = {
        dbAndTable.split("\\.") match {
            case Array(db, table) => dbAndtableToPath(db, table)
            case Array(table) => dbAndtableToPath("default", table)
            case _ => throw new RuntimeException(s"datalake table format error:${dbAndTable}")
        }
    }

    def listTables = {
        listPath(new Path(value)).flatMap { db =>
            val dbName = db.getPath.getName
            listPath(db.getPath).map { tablePath =>
                val tableName = tablePath.getPath.getName
                DeltaTableIdentifier(tableName, Option(dbName), None)
            }
        }
    }

    private def listPath(path: Path) = {
        val fs = FileSystem.get(new Configuration())
        fs.listStatus(path)
    }
}

object DataLake {
    val SPARK_DL_PATH = "spark.datalake.path"
    val STREAMING_DL_PATH = "streaming.datalake.path"
    val DELTA_LAKE_OVERWRITE_HIVE = "spark.yisql.datalake.overwrite.hive"
}

case class DeltaTableIdentifier(table: String, database: Option[String], operator: Option[String]) {
    val identifier: String = table

    def this(table: String) = this(table, None, None)
}
