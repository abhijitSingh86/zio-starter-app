package database

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import domain.config.AppConfig
import domain.config.AppConfig.DatabaseConfig
import zio._

object QuillDataSource {

  private def mkDataSource(config: DatabaseConfig): Task[HikariDataSource] = {
    for {
      pgDataSource <- ZIO.attempt {
        val dataSource = new org.postgresql.ds.PGSimpleDataSource()
        dataSource.setURL(config.url)
        dataSource.setUser(config.username)
        dataSource.setPassword(config.password)
        dataSource
      }
      hikariConfig <- ZIO.attempt {
        val config = new HikariConfig()
        config.setDataSource(pgDataSource)
        config
      }
      dataSource <- ZIO.attempt(new HikariDataSource(hikariConfig))
    } yield dataSource
  }

  val layer = ZLayer.fromZIO(ZIO.service[AppConfig].flatMap(config => mkDataSource(config.databaseConfig)))
}
