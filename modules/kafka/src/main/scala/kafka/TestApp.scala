package kafka

import domain.config.AppConfig
import domain.config.AppConfig.KafkaConfiguration
import org.apache.kafka.clients.producer.ProducerRecord
import zio.kafka.serde.Serde
import zio.stream.ZStream
import zio.{Clock, Schedule, Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer, durationInt}

object TestApp extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val config = KafkaConfiguration("sss", List("localhost:29092"), Map.empty, 10, "dlqTopic", List("someTopics"))
    val pro = for {
      pr <- ZIO.service[KafkaProducerService]
      s <- ZStream
        .repeatZIO(Clock.currentDateTime)
        .schedule(Schedule.spaced(1.second))
        .tap(_ => ZIO.logInfo("Inside"))
        .mapZIO(time => pr.sendMessage(config.topics.head, s"${time.getMinute}", s"$time -- Hello, World!"))
        .runDrain

    } yield s

    val cons = for {
      consumer <- ZIO.service[KafkaConsumerService]
      c <- consumer.consume(c =>
        ZIO.collectAll(c.map(x => ZIO.logInfo(s"${x.key} --  ${x.value}")).toList).map(_ => ())
      )
    } yield (c)

    pro
      .race(cons)
      .provide(
        KafkaProducerService.layer,
        KafkaConsumerService.layer,
        ZLayer.succeed(AppConfig(config, null)),
        Scope.default
      )

  }

}
