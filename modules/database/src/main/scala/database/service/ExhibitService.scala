package database.service
import io.getquill._
import io.getquill.jdbczio.Quill
import zio.{ZIO, ZLayer}

import java.sql.SQLException

trait ExhibitService {

  def get: ZIO[Any, SQLException, List[City]]

}

case class City(id: Int, name: String, countryCode: String, district: String, population: Long)

class LiveExhibitService(quill: Quill.Postgres[LowerCase]) extends ExhibitService {

  import quill._

  def get: ZIO[Any, SQLException, List[City]] = run(quote {
    query[City]
  })
}

object LiveExhibitService {
  def layer = ZLayer.fromFunction(new LiveExhibitService(_))
}
