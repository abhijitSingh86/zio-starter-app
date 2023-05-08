package app

import kafka.LiveProducer
import kafka.Consumer
import kafka.KafkaProducerService
import kafka.KafkaConfiguration
import org.apache.kafka.clients.producer.KafkaProducer
import zio.{ConfigProvider, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio._
import zio.kafka.producer.{Producer => ZProducer}
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.generic.ProductHint
import zio.kafka.consumer.CommittableRecord

object MainApp extends ZIOAppDefault {
  implicit def productHint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, CamelCase))

  def program(producer: ZProducer, kafkaConfiguration: KafkaConfiguration)(
      chunk: Chunk[CommittableRecord[String, String]]
  ): UIO[Unit] = {
    val con = new KafkaProducerService(producer, kafkaConfiguration)
    ZIO.logInfo(s"recieved some messages ${chunk.toList.map(_.key).mkString(",")}") *> ZIO.collectAll(chunk.map(x => con.sendDlqMessage(x.key, x.value)).toList).map(_ => ()).catchAll(e => ZIO.logError(s"Error $e"))
  }

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    for {

      config   <- ZIO.fromEither(ConfigSource.default.load[KafkaConfiguration])
      _        <- ZIO.logInfo(config.toString)
      producer <- LiveProducer.producer(config)
      consumer <- ZIO.attempt(Consumer.get(config))
      s        <- consumer.consume(config.topics)(program(producer,config))
    } yield (s)
  }
}
