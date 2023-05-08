package kafka

import domain.config.AppConfig
import zio._
import zio.kafka.consumer.{CommittableRecord, ConsumerSettings, Subscription, Consumer => ZConsumer}
import zio.kafka.serde.Serde
import domain.config.AppConfig._

trait KafkaConsumerService {

  def consume(body: Chunk[CommittableRecord[String, String]] => UIO[Unit]): ZIO[Scope, Any, Unit]

}

class LiveKafkaConsumerService(consumer: ZConsumer, kafkaConfiguration: KafkaConfiguration) extends KafkaConsumerService {

  override def consume(body: Chunk[CommittableRecord[String, String]] => UIO[Unit]): ZIO[Scope, Any, Unit] = {
    for {
      cc <- consumer
        .plainStream(
          Subscription.topics(kafkaConfiguration.topics.head, kafkaConfiguration.topics.tail: _*),
          Serde.string,
          Serde.string
        )
        .grouped(kafkaConfiguration.chunkSize)
        .tap(e => ZIO.logInfo(s" found ${e.toList.map(_.key).mkString(",")}"))
        .mapConcatZIO(a => body(a).map(_ => a.map(_.offset)))
        .aggregateAsync(ZConsumer.offsetBatches)
        .mapZIO(_.commit)
        .runDrain
    } yield cc
  }

}

object KafkaConsumerService {

  val layer = ZLayer.fromZIO(
    for {
      config <- ZIO.service[AppConfig]
      consumer <- ZConsumer.make(
        ConsumerSettings(config.kafkaConfiguration.bootstrapUrls)
          .withGroupId(config.kafkaConfiguration.groupId)
          .withProperties(config.kafkaConfiguration.props.toList: _*)
      )

    } yield new LiveKafkaConsumerService(consumer, config.kafkaConfiguration)
  )

}
