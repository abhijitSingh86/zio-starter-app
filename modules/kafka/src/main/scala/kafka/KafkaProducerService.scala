package kafka

import domain.config.AppConfig
import zio.{RIO, Scope, ZIO, ZLayer}
import zio.kafka.producer.{ProducerSettings, Producer => ZProducer}
import zio.kafka.serde.Serde
import domain.config.AppConfig._
import org.apache.kafka.clients.producer.RecordMetadata

trait KafkaProducer {
  def sendMessage(topic: String, key: String, message: String): RIO[Any, RecordMetadata]
  def sendDlqMessage(key: String, message: String): RIO[Any, RecordMetadata]
}

class KafkaProducerService(producer: ZProducer, kafkaConfiguration: KafkaConfiguration) extends KafkaProducer {
  def sendMessage(topic: String, key: String, message: String): RIO[Any, RecordMetadata] = {
    producer.produce(topic, key, message, Serde.string, Serde.string)
  }

  def sendDlqMessage(key: String, message: String): RIO[Any, RecordMetadata] = {
    producer.produce(kafkaConfiguration.dlqTopicName, key, message, Serde.string, Serde.string)
  }
}

object KafkaProducerService {

  val layer: ZLayer[Scope with AppConfig, Throwable, KafkaProducerService] = ZLayer.fromZIO {
    ZIO
      .service[AppConfig]
      .flatMap(x =>
        ZProducer
          .make(
            ProducerSettings(x.kafkaConfiguration.bootstrapUrls)
              .withProperties(x.kafkaConfiguration.props.toList: _*)
          )
          .map(p => new KafkaProducerService(p, x.kafkaConfiguration))
      )
  }

}
