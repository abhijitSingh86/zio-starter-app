import sbt._

object Library {

  object Version {}

  object Dependency {

    val scalaTest      = "org.scalatest" %% "scalatest"                     % "3.2.15"
    val novoCodeJunit  = "com.novocode"   % "junit-interface"               % "0.11"  
  }

  import Dependency._



  val testDependencies = Seq(
      scalaTest,
      novoCodeJunit
  ).map(c => c % "test")

}
