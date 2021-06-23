package com.zhy.yisql.addon.cmd.hive

import com.zhy.yisql.addon.cmd.hive.ByteUnit._
import com.zhy.yisql.common.utils.log.Logging
import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileStatus, FileSystem, Path}
import org.apache.orc.{OrcFile, Reader}
import org.apache.parquet.format.converter.ParquetMetadataConverter.NO_FILTER
import org.apache.parquet.hadoop.ParquetFileReader
import org.apache.parquet.hadoop.metadata.{BlockMetaData, ParquetMetadata}
import org.apache.parquet.hadoop.util.HiddenFileFilter
import org.apache.spark.sql.catalyst.TableIdentifier
import org.apache.spark.sql.catalyst.catalog.{CatalogTable, SessionCatalog}
import org.apache.spark.sql.{DataFrame, DataFrameWriter, Row, SparkSession}

import java.util
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.parallel.mutable.ParArray

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-04-13
 *  \* Time: 14:28
 *  \* Description: 
 *  \ */
object HiveUtils extends Logging {
  // 碎片文件的阈值
  private final val fragmentFileThreshold = 128 * MiB

  def parseDBAndTableFromStr(str: String, spark: SparkSession): CatalogTable = {
    val catalog: SessionCatalog = spark.sessionState.catalog
    var db: String = catalog.getCurrentDatabase
    var tableName: String = str
    val dbAndTable: Array[String] = str.split("\\.")
    if (dbAndTable.length > 1) {
      db = dbAndTable(0)
      tableName = dbAndTable.splitAt(1)._2.mkString(".")
    }
    catalog.getTableMetadata(TableIdentifier(tableName, Option(db)))
  }

  def mergePath(sparkSession: SparkSession, tableMeta: CatalogTable, location: String): Unit = {
    val conf: Configuration = sparkSession.sparkContext.hadoopConfiguration
    val fs: FileSystem = FileSystem.get(conf)

    if (!fs.exists(new Path(location))) {
      throw new RuntimeException(location + " does not exist!")
    }

    //获取访问时间
    var accessTime: Long = -28800000L //文件最后访问时间,默认"1970-01-01 00:00:00"
    var modificationTime: Long = -28800000L

    val inputFileList = new ListBuffer[FileStatus]()
    val mergePaths = new ListBuffer[String]()
    var fragmentFileCount = 0
    var totalSize = 0L
    var compression = "snappy"
    for (fileStatus <- fs.listStatus(new Path(location), HiddenFileFilter.INSTANCE)) {
      val path: Path = fileStatus.getPath

      if (fileStatus.isFile && isFragmentFile(path, fs, fragmentFileThreshold)) {
        fragmentFileCount = fragmentFileCount + 1
        inputFileList += fileStatus
        mergePaths += path.toString
        totalSize = totalSize + fileStatus.getLen
      }

      if (StringUtils.equals(compression, "snappy")
        && StringUtils.endsWithIgnoreCase(path.getName, ".zstd.parquet")) {
        compression = "zstd"
      }

      //设置访问时间和修改时间 所有文件中取最大值
      val fileAccessTime: Long = fileStatus.getAccessTime
      val fileModificationTime: Long = fileStatus.getModificationTime
      if (fileAccessTime != null && fileAccessTime > accessTime) {
        accessTime = fileAccessTime
      }
      if (fileModificationTime != null && fileModificationTime > modificationTime) {
        modificationTime = fileModificationTime
      }
    }

    // 如果碎片文件少于两个则不合并
    if (fragmentFileCount <= 2) {
      logInfo("fragment files <= 2, merge stopped")
      return
    }

    val mergeNum: Int = getFileCount(sparkSession, totalSize)

    sparkSession.conf.set("spark.merge.file.path", location)

    val time: Long = System.nanoTime()
    logInfo(s"prepare to merge data under path:$location total ${inputFileList.size} files")

    val tempDir: String = location + "/.mergeTemp"
    val tempPath = new Path(tempDir)
    if (fs.exists(tempPath)) {
      logInfo(tempDir + " 已经存在")
      fs.delete(tempPath, true)
    }

    val fileType: String = getTableFileType(tableMeta)
    val beforeRowCount: Long =
      if ("orc" == fileType) statRowCount(conf, inputFileList.toArray, orcCount)
      else statRowCount(conf, inputFileList.toArray, parquetCount)

    if ("orc" == fileType) {
      val df: DataFrame = sparkSession.read.orc(mergePaths: _*)
      //小于 20G 使用 repartition，大于 20G 使用 coalesce
      if (totalSize < 20 * GiB) {
        val dfw: DataFrameWriter[Row] = df.repartition(mergeNum).write.option("compression", compression)
        //        addTableOrcOption(tableMeta, dfw)
        dfw.orc(tempDir)
      } else {
        val dfw: DataFrameWriter[Row] = df.coalesce(mergeNum).write.option("compression", compression)
        //        addTableOrcOption(tableMeta, dfw)
        dfw.orc(tempDir)
      }
    } else {
      val df: DataFrame = sparkSession.read.parquet(mergePaths: _*)
      //小于 20G 使用 repartition，大于 20G 使用 coalesce
      if (totalSize < 20 * GiB) {
        df.repartition(mergeNum).write.option("compression", compression).parquet(tempDir)
      } else {
        df.coalesce(mergeNum).write.option("compression", compression).parquet(tempDir)
      }
    }

    val afterRowCount: Long =
      if ("orc" == fileType) statRowCount(conf, getInputFiles(conf, tempDir), orcCount)
      else statRowCount(conf, getInputFiles(conf, tempDir), parquetCount)

    if (beforeRowCount != afterRowCount) {
      fs.delete(tempPath, true)
      logInfo(s"合并前后文件记录数不一致，退出合并，合并前：$beforeRowCount, 合并后：$afterRowCount")
      return
    }

    sparkSession.conf.set("spark.merge.file.count", mergeNum)
    sparkSession.conf.set("spark.merge.record.count", afterRowCount)

    val destList: util.List[Path] = MergeTableUtils.getPathFromDirectory(sparkSession.sparkContext.hadoopConfiguration, tempDir)
    for (path <- destList) {
      if (path.getName.endsWith("parquet") || path.getName.endsWith("orc")) {
        val newLocation: String = location + "/" + path.getName
        fs.rename(path, new Path(newLocation))
        fs.setTimes(new Path(newLocation), modificationTime, accessTime) //设置访问时间

        logInfo("merge file: " + newLocation)
      }
    }

    for (path <- inputFileList) {
      fs.delete(path.getPath, true)
    }
    fs.delete(tempPath, true)

    //    val tableName = tableMeta.identifier.table
    //    if (StringUtils.contains(location, tableName)) {
    //      val partition = StringUtils.substringAfter(location, tableName + "/")
    //      SparkParserFactory.mergePartitionList.add(partition)
    //    }
    logInfo(s"merge finished, cost ${TimeUnit.MICROSECONDS.toMillis(System.nanoTime() - time)} ms")
  }

  def mergeRecursive(sparkSession: SparkSession, tableMeta: CatalogTable, location: String): Unit = {
    val fs: FileSystem = FileSystem.get(sparkSession.sparkContext.hadoopConfiguration)
    var hasFile = true
    for (status <- fs.listStatus(new Path(location), HiddenFileFilter.INSTANCE)) {
      if (status.isDirectory) {
        mergeRecursive(sparkSession, tableMeta, status.getPath.toString)
        hasFile = false
      }
    }

    if (hasFile) {
      mergePath(sparkSession, tableMeta, location)
    }
  }

  private def getInputFiles(conf: Configuration, dir: String): Array[FileStatus] = {
    val dirPath: Path = new Path(dir)
    val fileSystem: FileSystem = FileSystem.get(conf)
    val inputFiles: Array[FileStatus] = fileSystem.listStatus(dirPath, HiddenFileFilter.INSTANCE)
    inputFiles
  }

  private def parquetCount(fileStatus: FileStatus, conf: Configuration, fileRowCount: AtomicLong): Unit = {
    val parquetMetadata: ParquetMetadata = ParquetFileReader.readFooter(conf, fileStatus, NO_FILTER)
    val blockMetaDataList: util.List[BlockMetaData] = parquetMetadata.getBlocks
    for (b <- blockMetaDataList) {
      fileRowCount.addAndGet(b.getRowCount)
    }
  }

  private def orcCount(fileStatus: FileStatus, conf: Configuration, fileRowCount: AtomicLong): Unit = {
    val reader: Reader = OrcFile.createReader(fileStatus.getPath, OrcFile.readerOptions(conf))
    fileRowCount.addAndGet(reader.getNumberOfRows)
  }

  private def statRowCount(conf: Configuration, inputFiles: Array[FileStatus],
                           f: (FileStatus, Configuration, AtomicLong) => Unit): Long = {
    val fileRowCount = new AtomicLong(0)
    try {
      val parFiles: ParArray[FileStatus] = ParArray(inputFiles: _*)
      parFiles.tasksupport = new ForkJoinTaskSupport(new scala.concurrent.forkjoin.ForkJoinPool(3))

      parFiles.foreach((fileStatus: FileStatus) => {
        f(fileStatus, conf, fileRowCount)
      });
    } catch {
      case e: Exception => logError(e.getMessage)
    }
    fileRowCount.get()
  }

  /** 判断是否为碎片文件,默认小于 64M 定义为碎片文件 */
  private def isFragmentFile(path: Path,
                             fs: FileSystem,
                             fragmentFileThreshold: Long): Boolean = {
    if (!path.getName.startsWith(".") && !path.getName.equals("_SUCCESS")) {
      val fileLength: Long = fs.getFileStatus(path).getLen
      if (fileLength <= fragmentFileThreshold) {
        return true
      }
    }
    false
  }

  /**
   * 计算分区数量
   */
  private def getFileCount(sparkSession: SparkSession, totalSize: Long): Int = {
    var count = 0L
    val totalTasks: Int = getMaxConcurrent(sparkSession)

    if (totalTasks == 1) {
      count = totalSize / (512 * MiB)
    } else {
      count = totalSize / totalTasks match {
        case c if c < 256 * MiB => totalSize / (256 * MiB)
        case _ => totalSize / (512 * MiB)
      }
    }
    count.toInt + 1
  }

  private def getMaxConcurrent(spark: SparkSession): Int = {
    val dynamicExecutorNum: Int = spark.conf.getOption("spark.dynamicAllocation.maxExecutors").getOrElse(1).toString.toInt
    val executorInstance: Int = spark.conf.getOption("spark.executor.instances").getOrElse(1).toString.toInt
    val executorCores: Int = spark.conf.getOption("spark.executor.cores").getOrElse(1).toString.toInt

    val totalTasks: Int = if (executorInstance >= dynamicExecutorNum) {
      executorInstance * executorCores
    } else {
      dynamicExecutorNum * executorCores
    }

    totalTasks
  }

  /**
   * merge 丢失orc 参数配置https://issues.apache.org/jira/browse/SPARK-12417
   *
   * @param tableMeta
   * @param dfw
   */
  private def addTableOrcOption(tableMeta: CatalogTable, dfw: DataFrameWriter[Row]): Unit = {
    tableMeta.properties.foreach {
      case (key, value) => {
        if (StringUtils.startsWith(key, "orc.")) {
          dfw.option(key, value)
        }
      }
    }
  }

  private def getTableFileType(tableMeta: CatalogTable): String = {
    if (tableMeta.storage.inputFormat.get == "org.apache.hadoop.hive.ql.io.orc.OrcInputFormat") {
      "orc"
    } else {
      "parquet"
    }
  }
}

object ByteUnit {
  val MiB: Long = 1048576
  val GiB: Long = 1024 * MiB
}