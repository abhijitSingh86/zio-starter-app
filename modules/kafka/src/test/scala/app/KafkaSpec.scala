package app

import io.github.embeddedkafka.EmbeddedKafka.consumeFirstMessageFrom
import io.github.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.common.serialization.{Deserializer, Serializer, StringDeserializer, StringSerializer}
import zio.kafka.consumer.{Consumer, ConsumerSettings, Subscription}
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde.Serde
import zio.{Cause, Scope, ZIO}
import zio.test.{Spec, TestAspect, TestEnvironment, ZIOSpecDefault, assertTrue}

object KafkaSpec extends ZIOSpecDefault {

  implicit val config                             = EmbeddedKafkaConfig(kafkaPort = 12345)
  implicit val serializer: Serializer[String]     = new StringSerializer()
  implicit val deserializer: Deserializer[String] = new StringDeserializer()
  val bootstrapUrl                                = "localhost:12345"
  private def stopContainer = {
    ZIO
      .attempt(EmbeddedKafka.stop())
      .catchAll(ex => ZIO.logErrorCause("Error while closing the kafka container", Cause.die(ex)))
  }

  private def startContainer = ZIO.attempt {
    EmbeddedKafka.start()
  }

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("Kafka demo Test")(
    test("Should be able to send a message to kafka and validate the correct message is posted") {
      val messageTobePosted = "someSecretMessage"
      for {
        p             <- Producer.make(ProducerSettings(List(bootstrapUrl)))
        _             <- ZIO.logInfo("Producing the message")
        r             <- p.produce("myTopic", "key", messageTobePosted, Serde.string, Serde.string)
        _             <- ZIO.logInfo(s"test message send to kafka ${r.topic()} ${r.offset()} ${r.partition()}")
        readFromTopic <- ZIO.attempt(consumeFirstMessageFrom("myTopic"))
      } yield assertTrue(readFromTopic == messageTobePosted)
    },
    test("Should be able to send a message to kafka and consume it using a kafka consumer") {
      val messageTobePosted = "someSecretMessage"
      for {
        p <- Producer.make(ProducerSettings(List(bootstrapUrl)))
        _ <- ZIO.logInfo("Producing the message")
        r <- p.produce("myTopic", "key", messageTobePosted, Serde.string, Serde.string)
        _ <- ZIO.logInfo(s"test message send to kafka ${r.topic()} ${r.offset()} ${r.partition()}")
        c <- Consumer.make(
          ConsumerSettings(List(bootstrapUrl))
            .withGroupId("testGroup")
            .withProperties(("allow.auto.create.topics", "false"))
            .withProperties(("enable.auto.commit ", "true"))
        )
        _ <- ZIO.logInfo("Consumer crated for test kafka")
        readFromTopic <- c
          .plainStream(Subscription.topics("myTopic"), Serde.string, Serde.string)
          .tap(e => ZIO.logInfo(s"${e.key} -- ${e.value}"))
          .take(1)
          .runHead
      } yield assertTrue(readFromTopic.get.value == messageTobePosted)
    }
  ) @@ TestAspect.beforeAll(startContainer) @@ TestAspect.afterAll(stopContainer)
}
