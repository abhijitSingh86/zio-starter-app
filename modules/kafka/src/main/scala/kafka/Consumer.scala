package kafka

import zio._
import zio.kafka.consumer.{CommittableRecord, ConsumerSettings, Subscription, Consumer => ZConsumer}
import zio.kafka.serde.Serde

trait Consumer {

  def consume(topics: List[String])(body: Chunk[CommittableRecord[String, String]] => UIO[Unit]): ZIO[Scope, Any, Unit]

}

case class KafkaConfiguration(
    groupId: String,
    bootstrapUrls: List[String],
    props: Map[String, String],
    chunkSize: Int,
    dlqTopicName: String,
    topics: List[String]
)

class LiveConsumer(config: KafkaConfiguration) extends Consumer {

  override def consume(
      topics: List[String]
  )(body: Chunk[CommittableRecord[String, String]] => UIO[Unit]): ZIO[Scope, Any, Unit] = {
    for {
      c <- ZConsumer.make(
        ConsumerSettings(config.bootstrapUrls)
          .withGroupId(config.groupId)
          .withProperties(config.props.toList: _*)
      )
      cc <- c
        .plainStream(Subscription.topics(topics.head, topics.tail: _*), Serde.string, Serde.string)
        .grouped(config.chunkSize)
        .tap(e => ZIO.logInfo(s" found ${e.toList.map(_.key).mkString(",")}"))
        .mapConcatZIO(a => body(a).map(_ => a.map(_.offset)))
        .aggregateAsync(ZConsumer.offsetBatches)
        .mapZIO(_.commit)
        .runDrain
    } yield cc
  }

}

object Consumer {
  def get(kafkaConfiguration: KafkaConfiguration) = new LiveConsumer(config = kafkaConfiguration)
}
