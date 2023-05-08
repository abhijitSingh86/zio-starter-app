package database.config

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.Location
import zio.ZIO

case class DatabaseConfig(url: String, username: String, password: String)

object DatabaseMigrator {

  def migrate(config: DatabaseConfig): ZIO[Any, Throwable, Unit] = for {
    _ <- ZIO.logInfo("Beginning Db migration")
    flyway <- ZIO.attempt(
      Flyway
        .configure()
        .cleanDisabled(false)
        .mixed(true)
        .locations(new Location( "filesystem:modules/database/src/main/resources/db/migration"))
        .baselineOnMigrate(true)
        .dataSource(config.url, config.username, config.password)
        .load()
    )
    _ <- ZIO.attempt {
      flyway.migrate() }
    _ <- ZIO.logInfo("Finished Db migration")
  } yield ()
}
