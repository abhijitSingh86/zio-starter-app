package domain.config

import domain.config.AppConfig.{DatabaseConfig, KafkaConfiguration}

case class AppConfig(kafkaConfiguration: KafkaConfiguration, databaseConfig: DatabaseConfig)

object AppConfig {
  case class KafkaConfiguration(
      groupId: String,
      bootstrapUrls: List[String],
      props: Map[String, String],
      chunkSize: Int,
      dlqTopicName: String,
      topics: List[String]
  )

  case class DatabaseConfig(url: String, username: String, password: String)
}
