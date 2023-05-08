ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.10"

val zioVersion = "2.0.10"
val zioLogging = "2.1.1"

val logging = Seq(
  "ch.qos.logback" % "logback-classic" % "1.4.6"
) ++ Seq(
  "dev.zio" %% "zio-logging",
  "dev.zio" %% "zio-logging-slf4j"
).map(_ % zioLogging)

lazy val database = (project in file("./modules/database"))
  .settings(
    name := "database_module",
    libraryDependencies ++= Seq(
      "org.postgresql"     % "postgresql"           % "42.5.4",
      "io.getquill"       %% "quill-jdbc-zio"       % "4.6.0",
      "dev.zio"           %% "zio"                  % zioVersion,
      "dev.zio"           %% "zio-streams"          % zioVersion,
      "org.flywaydb"       % "flyway-core"          % "9.17.0",
      "dev.zio"           %% "zio-test"             % zioVersion % Test,
      "dev.zio"           %% "zio-test-sbt"         % zioVersion % Test,
      "com.dimafeng"      %% "testcontainers-scala" % "0.40.12"  % Test,
      "org.testcontainers" % "postgresql"           % "1.18.0"   % Test
    ) ++ Library.testDependencies ++ logging
  )

lazy val kafka = (project in file("./modules/kafka"))
  .settings(
    name := "kafka_module",
    libraryDependencies ++= Seq(
      "dev.zio"                 %% "zio-kafka"          % "2.2",
      "dev.zio"                 %% "zio"                % zioVersion,
      "dev.zio"                 %% "zio-streams"        % zioVersion,
      "dev.zio"                 %% "zio-logging"        % "2.1.12",
      "dev.zio"                 %% "zio-logging-slf4j2" % "2.1.12",
      "ch.qos.logback"           % "logback-classic"    % "1.4.7",
      "dev.zio"                 %% "zio-test"           % zioVersion % Test,
      "io.github.embeddedkafka" %% "embedded-kafka"     % "3.4.0"    % Test
    ) ++ Library.testDependencies
  )

lazy val delivery = (project in file("./modules/delivery"))
  .settings(
    name := "delivery_module",
    libraryDependencies ++= Seq(
      "dev.zio"         %% "zio"          % zioVersion,
      "dev.zio"         %% "zio-streams"  % zioVersion,
      "dev.zio"         %% "zio-http"     % "0.0.5",
      "dev.zio"         %% "zio-json"     % "0.4.2",
      "org.hdrhistogram" % "HdrHistogram" % "2.1.12",
      "dev.zio"         %% "zio-test"     % zioVersion % Test
    ) ++ Library.testDependencies
  )
  .dependsOn(database)

lazy val ruleEngine = (project in file("./modules/ruleEngine"))
  .settings(
    name := "ruleEngine_module",
    libraryDependencies ++= Seq(
      "dev.zio"         %% "zio"          % zioVersion,
      "dev.zio"         %% "zio-streams"  % zioVersion,
      "dev.zio"         %% "zio-http"     % "0.0.5",
      "dev.zio"         %% "zio-json"     % "0.4.2",
      "org.hdrhistogram" % "HdrHistogram" % "2.1.12",
      "dev.zio"         %% "zio-test"     % zioVersion % Test
    ) ++ Library.testDependencies
  )
  .dependsOn(database)

lazy val root = (project in file("."))
  .settings(
    name := "SomeApp",
    libraryDependencies ++= Seq(
      "dev.zio"               %% "zio"          % zioVersion,
      "dev.zio"               %% "zio-streams"  % zioVersion,
      "dev.zio"               %% "zio-config"   % "3.0.7",
      "com.github.pureconfig" %% "pureconfig"   % "0.17.3",
      "org.hdrhistogram"       % "HdrHistogram" % "2.1.12",
      "dev.zio"               %% "zio-test"     % zioVersion % Test
    ) ++ Library.testDependencies
  )
  .dependsOn(kafka, database)
