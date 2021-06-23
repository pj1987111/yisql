package org.apache.spark.sql.jdbc

import org.apache.spark.sql.execution.datasources.jdbc.{JDBCOptions, JdbcOptionsInWrite, JdbcUtils}
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.{DataFrame, DataFrameWriter, Row, SaveMode}

import java.lang.reflect.Field
import java.sql.{Connection, Statement}

object DataFrameWriterExtensions {

  implicit class Upsert(w: DataFrameWriter[Row]) {
    def upsert(idCol: Option[String], jdbcOptions: JDBCOptions, df: DataFrame): Unit = {
      val idColumn: Option[Seq[StructField]] = idCol.map((f: String) => df.schema.filter((s: StructField) => f.split(",").contains(s.name)))
      val url: String = jdbcOptions.url
      val table: String = jdbcOptions.tableOrQuery
      val modeF: Field = w.getClass.getDeclaredField("mode")
      modeF.setAccessible(true)
      val mode: SaveMode = modeF.get(w).asInstanceOf[SaveMode]
      val conn: Connection = JdbcUtils.createConnectionFactory(jdbcOptions)()
      val isCaseSensitive: Boolean = df.sqlContext.conf.caseSensitiveAnalysis
      val writeOption = new JdbcOptionsInWrite(url, table, jdbcOptions.parameters)
      try {

        var tableExists: Boolean = JdbcUtils.tableExists(conn, writeOption)

        if (mode == SaveMode.Ignore && tableExists) {
          return
        }

        if (mode == SaveMode.ErrorIfExists && tableExists) {
          sys.error(s"Table $table already exists.")
        }

        if (mode == SaveMode.Overwrite && tableExists) {
          JdbcUtils.dropTable(conn, table, writeOption)
          tableExists = false
        }

        // Create the table if the table didn't exist.
        if (!tableExists) {
          val schema: String = JdbcUtils.schemaString(df, url, jdbcOptions.createTableColumnTypes)
          val dialect: JdbcDialect = JdbcDialects.get(url)
          val pk: String = idColumn.map { f: Seq[StructField] =>
            val key: String = f.map((c: StructField) => s"${dialect.quoteIdentifier(c.name)}").mkString(",")
            s", primary key(${key})"
          }.getOrElse("")
          val sql = s"CREATE TABLE $table ( $schema $pk )"
          val statement: Statement = conn.createStatement
          try {
            statement.executeUpdate(sql)
          } finally {
            statement.close()
          }
        }
      } finally {
        conn.close()
      }

      //todo: make this a single method
      idColumn match {
        case Some(id) => UpsertUtils.upsert(df, idColumn, jdbcOptions, isCaseSensitive)
        case None => JdbcUtils.saveTable(df, Some(df.schema), isCaseSensitive, options = writeOption)
      }

    }

  }

}
