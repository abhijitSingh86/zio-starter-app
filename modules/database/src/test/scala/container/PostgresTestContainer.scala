package container

import com.dimafeng.testcontainers.{JdbcDatabaseContainer, PostgreSQLContainer}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import database.config.DatabaseMigrator
import domain.config.AppConfig
import domain.config.AppConfig.DatabaseConfig
import io.getquill._
import org.testcontainers.utility.DockerImageName
import zio.{Cause, ZIO, ZLayer}
import zio.test.{TestAspect, ZIOSpecDefault, assertTrue}

object PostgresTestContainer extends ZIOSpecDefault {

  val container = new PostgreSQLContainer(
    Option(DockerImageName.parse("postgres:10")),
    commonJdbcParams = JdbcDatabaseContainer.CommonParams(initScriptPath = Option("db/init.sql"))
  )

  lazy val databaseConfig = DatabaseConfig(container.jdbcUrl,container.username, container.password)
//  lazy val databaseConfig = DatabaseConfig("jdbc:postgres://localhost:5432/localDb","dbuser", "dbsecret")
  lazy val config = {
    val c = new HikariConfig()
    c.setJdbcUrl(databaseConfig.url)
    c.setPassword(databaseConfig.password)
    c.setUsername(databaseConfig.username)
    c
  }
  lazy val datasource = new PostgresJdbcContext(LowerCase, new HikariDataSource(config))

  private def stopContainer() = {
    ZIO
      .attempt(container.stop())
      .catchAll(ex => ZIO.logErrorCause("Error while closing the database", Cause.die(ex)))
  }
  def spec =
    (suite("PostgresTestContainer")(
      test("should be able to spin up the container and run the init sql and query custom query") {
        import datasource._
        val pi  = quote(sql""" select id from city""".as[Query[Int]])
        val ret = datasource.run(pi)
        assertTrue(ret.size == 30)
      }
    ) @@ TestAspect.beforeAll(DatabaseMigrator.migrate) @@
      TestAspect.afterAll(stopContainer())).provide(DatabaseMigrator.layer, ZLayer.succeed{container.start();AppConfig(null, databaseConfig)})

}
