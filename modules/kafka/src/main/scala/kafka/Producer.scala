package kafka

import zio.kafka.producer
import zio.{Scope, ZIO}
import zio.kafka.producer.{Producer => ZProducer, ProducerSettings}
import zio.kafka.serde.Serde

trait Producer {}

object LiveProducer {

  def producer(configuration: KafkaConfiguration): ZIO[Scope, Throwable, ZProducer] = {
    for {
      p <- ZProducer.make(
        ProducerSettings(configuration.bootstrapUrls)
          .withProperties(configuration.props.toList: _*)
      )
    } yield p
  }
}

class KafkaProducerService(producer: ZProducer, kafkaConfiguration: KafkaConfiguration) {
  def sendMessage(topic: String, key: String, message: String) = {
    producer.produce(topic, key, message, Serde.string, Serde.string)
  }

  def sendDlqMessage(key: String, message: String) = {
    producer.produce(kafkaConfiguration.dlqTopicName, key, message, Serde.string, Serde.string)
  }
}
