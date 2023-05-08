package kafka

import org.apache.kafka.clients.producer.ProducerRecord
import zio.kafka.serde.Serde
import zio.stream.ZStream
import zio.{Clock, Schedule, Scope, ZIO, ZIOAppArgs, ZIOAppDefault, durationInt}

object TestApp extends ZIOAppDefault{

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val config = KafkaConfiguration("sss",List("localhost:29092"),Map.empty,10,"dlqTopic",List("someTopics"))
    val pro = for{
      pr <-LiveProducer.producer(config)
      s <- ZStream
        .repeatZIO(Clock.currentDateTime)
        .schedule(Schedule.spaced(1.second))
        .tap(_ => ZIO.logInfo("Inside"))
        .map(time => new ProducerRecord(config.topics.head, s"${time.getMinute}", s"$time -- Hello, World!"))
        .mapZIO(s => pr.produce(s, Serde.string, Serde.string))
        .runDrain
    }yield s

    val cons = for{
      c <- Consumer.get(config).consume(List(config.dlqTopicName))(c => ZIO.collectAll(c.map(x => ZIO.logInfo(s"${x.key} --  ${x.value}")).toList).map(_ => ()))
    }yield (c)

    pro.race(cons)
  }

}
