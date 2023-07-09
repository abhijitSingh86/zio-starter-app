package layers

import domain.config.AppConfig
import pureconfig.generic.ProductHint
import pureconfig.{CamelCase, ConfigFieldMapping, ConfigSource}
import zio.{ZIO, ZLayer}
import pureconfig._
import pureconfig.generic.auto._

object ConfigLayer {

  implicit def productHint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, CamelCase))
  val layer                   = ZLayer.fromZIO(ZIO.fromEither(ConfigSource.default.load[AppConfig]))

}
