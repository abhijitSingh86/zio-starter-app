package database.config

import domain.config.AppConfig
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import zio.{ZIO, ZLayer}
import domain.config.AppConfig._

class DatabaseMigrator(config: DatabaseConfig) {

  def migrate: ZIO[Any, Throwable, Unit] = for {
    _ <- ZIO.logInfo("Beginning Db migration")
    flyway <- ZIO.attempt(
      Flyway
        .configure()
        .cleanDisabled(false)
        .mixed(true)
        .locations(new Location("filesystem:modules/database/src/main/resources/db/migration"))
        .baselineOnMigrate(true)
        .dataSource(config.url, config.username, config.password)
        .load()
    )
    r <- ZIO.attempt { flyway.migrate() }
    _ <- ZIO.logInfo(s"Finished Db migration is ${r.success}, migrations executed ${r.migrationsExecuted}")
  } yield ()
}

object DatabaseMigrator {
  val layer = ZLayer.fromZIO {
    ZIO.service[AppConfig].map(ac => new DatabaseMigrator(ac.databaseConfig))
  }

  def migrate = for {
    s   <- ZIO.service[DatabaseMigrator]
    res <- s.migrate
  } yield res

}
