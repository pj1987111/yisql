package org.apache.spark.sql.kafka010

import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.util.UninterruptibleThread

import java.util.{Locale, UUID}

/**
 *  \* Created with IntelliJ IDEA.
 *  \* User: hongyi.zhou
 *  \* Date: 2021-03-06
 *  \* Time: 09:38
 *  \* Description: 
 *  \ */
object KafkaOffsetInfo {
  def getKafkaInfo(spark: SparkSession, params: Map[String, String]): (KafkaSourceOffset, KafkaSourceOffset) = {
    val parameters: Map[String, String] = params
    val caseInsensitiveParams: Map[String, String] = parameters.map { case (k, v) => (k.toLowerCase(Locale.ROOT), v) }
    val specifiedKafkaParams: Map[String, String] =
      parameters
        .keySet
        .filter((_: String).toLowerCase(Locale.ROOT).startsWith("kafka."))
        .map { k => k.drop(6) -> parameters(k) }
        .toMap
    val uniqueGroupId: String = UUID.randomUUID().toString
    val kafkaOffsetReader = new KafkaOffsetReader(
      strategy(caseInsensitiveParams),
      KafkaSourceProvider.kafkaParamsForDriver(specifiedKafkaParams),
      parameters,
      driverGroupIdPrefix = s"$uniqueGroupId-driver")

    var newUntilPartitionOffsets: KafkaSourceOffset = null
    var newStartPartitionOffsets: KafkaSourceOffset = null

    try {
      // Leverage the KafkaReader to obtain the relevant partition offsets
      // we always get the LatestOffsetRangeLimit
      val untilPartitionOffsets: Map[TopicPartition, Long] = getPartitionOffsets(kafkaOffsetReader, LatestOffsetRangeLimit)

      def reportDataLoss(message: String): Unit = {
        if (params.getOrElse("failOnDataLoss", "true").toBoolean) {
          throw new IllegalStateException(message + s". $INSTRUCTION_FOR_FAIL_ON_DATA_LOSS_TRUE")
        } else {
          //                    logWarning(message + s". $INSTRUCTION_FOR_FAIL_ON_DATA_LOSS_FALSE")
        }
      }

      val wow = new UninterruptibleThread(new Runnable {
        override def run(): Unit = {
          newUntilPartitionOffsets = kafkaOffsetReader.fetchSpecificOffsets(untilPartitionOffsets, reportDataLoss)
          val sampleNum: Long = params.getOrElse("sampleNum", "100").toLong
          val startPartitionOffsets: Map[TopicPartition, Long] = newUntilPartitionOffsets.partitionToOffsets.map { f: (TopicPartition, Long) => (f._1, f._2 - sampleNum) }
          newStartPartitionOffsets = kafkaOffsetReader.fetchSpecificOffsets(startPartitionOffsets, reportDataLoss)
        }
      }, UUID.randomUUID() + "-driver-fetch-untilPartitionOffsets")
      wow.start()
      wow.join()
    } finally {
      kafkaOffsetReader.close()
    }

    (newStartPartitionOffsets, newUntilPartitionOffsets)

  }

  def getPartitionOffsets(
                           kafkaReader: KafkaOffsetReader,
                           kafkaOffsets: KafkaOffsetRangeLimit): Map[TopicPartition, Long] = {
    def validateTopicPartitions(partitions: Set[TopicPartition],
                                partitionOffsets: Map[TopicPartition, Long]): Map[TopicPartition, Long] = {
      assert(partitions == partitionOffsets.keySet,
        "If startingOffsets contains specific offsets, you must specify all TopicPartitions.\n" +
          "Use -1 for latest, -2 for earliest, if you don't care.\n" +
          s"Specified: ${partitionOffsets.keySet} Assigned: ${partitions}")
      //            logDebug(s"Partitions assigned to consumer: $partitions. Seeking to $partitionOffsets")
      partitionOffsets
    }

    val partitions: Set[TopicPartition] = kafkaReader.fetchTopicPartitions()
    // Obtain TopicPartition offsets with late binding support
    kafkaOffsets match {
      case EarliestOffsetRangeLimit => partitions.map {
        case tp => tp -> KafkaOffsetRangeLimit.EARLIEST
      }.toMap
      case LatestOffsetRangeLimit => partitions.map {
        case tp => tp -> KafkaOffsetRangeLimit.LATEST
      }.toMap
      case SpecificOffsetRangeLimit(partitionOffsets) =>
        validateTopicPartitions(partitions, partitionOffsets)
    }
  }

  def strategy(caseInsensitiveParams: Map[String, String]): ConsumerStrategy =
    caseInsensitiveParams.find(x => STRATEGY_OPTION_KEYS.contains(x._1)).get match {
      case ("assign", value) =>
        AssignStrategy(JsonUtils.partitions(value))
      case ("subscribe", value) =>
        SubscribeStrategy(value.split(",").map(_.trim()).filter(_.nonEmpty))
      case ("subscribepattern", value) =>
        SubscribePatternStrategy(value.trim())
      case _ =>
        // Should never reach here as we are already matching on
        // matched strategy names
        throw new IllegalArgumentException("Unknown option")
    }


  private val STRATEGY_OPTION_KEYS = Set("subscribe", "subscribepattern", "assign")
  private[kafka010] val STARTING_OFFSETS_OPTION_KEY = "startingoffsets"
  private[kafka010] val ENDING_OFFSETS_OPTION_KEY = "endingoffsets"
  private val FAIL_ON_DATA_LOSS_OPTION_KEY = "failondataloss"
  private val MIN_PARTITIONS_OPTION_KEY = "minpartitions"

  val TOPIC_OPTION_KEY = "topic"

  val INSTRUCTION_FOR_FAIL_ON_DATA_LOSS_FALSE: String =
    """
      |Some data may have been lost because they are not available in Kafka any more; either the
      | data was aged out by Kafka or the topic may have been deleted before all the data in the
      | topic was processed. If you want your streaming query to fail on such cases, set the source
      | option "failOnDataLoss" to "true".
        """.stripMargin

  val INSTRUCTION_FOR_FAIL_ON_DATA_LOSS_TRUE: String =
    """
      |Some data may have been lost because they are not available in Kafka any more; either the
      | data was aged out by Kafka or the topic may have been deleted before all the data in the
      | topic was processed. If you don't want your streaming query to fail on such cases, set the
      | source option "failOnDataLoss" to "false".
        """.stripMargin


  private val deserClassName = classOf[ByteArrayDeserializer].getName
}
