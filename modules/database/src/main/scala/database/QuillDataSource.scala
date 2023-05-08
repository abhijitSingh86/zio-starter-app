package database

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import database.config.DatabaseConfig
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

  def live(config: DatabaseConfig): ZLayer[Any, Throwable, HikariDataSource] =
    ZLayer.fromZIO(mkDataSource(config))
}
