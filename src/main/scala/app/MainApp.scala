package app

import database.QuillDataSource
import database.config.DatabaseMigrator
import database.service.{ExhibitService, LiveExhibitService}
import io.getquill.{LowerCase, SnakeCase}
import io.getquill.jdbczio.Quill
import kafka.KafkaConsumerService
import kafka.KafkaProducerService
import layers.ConfigLayer
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}
import zio._
import zio.kafka.consumer.CommittableRecord

object MainApp extends ZIOAppDefault {

  def program(
      producer: KafkaProducerService,
      chunk: Chunk[CommittableRecord[String, String]]
  ): ZIO[Any, Nothing, Unit] = for {
    _ <- ZIO.logInfo(s"received some messages ${chunk.toList.map(_.key).mkString(",")}")
    _ <- ZIO
      .collectAll(chunk.map(x => producer.sendDlqMessage(x.key, x.value)).toList)
      .map(_ => ())
      .catchAll(e => ZIO.logError(s"Error $e"))
  } yield ()

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Unit] = {
    val prg = for {
      _        <- DatabaseMigrator.migrate
      consumer <- ZIO.service[KafkaConsumerService]
      producer <- ZIO.service[KafkaProducerService]
      dp       <- ZIO.service[ExhibitService]
      cities   <- dp.get
      _        <- ZIO.logInfo(s" ${cities.mkString("\n")}")
      _        <- consumer.consume(x => program(producer, x))
    } yield ()

    prg.provide(
      ConfigLayer.layer,
      KafkaConsumerService.layer,
      KafkaProducerService.layer,
      DatabaseMigrator.layer,
      QuillDataSource.layer,
      Scope.default,
      LiveExhibitService.layer,
      Quill.Postgres.fromNamingStrategy(LowerCase)
    )

  }
}
