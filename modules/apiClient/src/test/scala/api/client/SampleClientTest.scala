package api.client

import zio.{Ref, Scope, ZIO, ZLayer}
import zio.http._
import zio.http.model.Method
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue}

class SampleClientTest extends ZIOSpecDefault {

  def startWebServer(url: String, response: Response) = {
    val app: HttpApp[Any, Nothing] = Http.collect[Request] { case Method.GET -> !! / url =>
      response
    }
    Server.serve(app)
  }

//  def layer = {
//    ZLayer.scoped(
//      ZIO.acquireRelease(
//        startWebServer()
//      )(c => c.get.debug("Number of tests executed"))
//    )
//  }

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("Sample client ")(
    test("Should get the response") {
      assertTrue(true)
    },
    test("") {
      assertTrue(true)
    }
  ).provide(Server.default)
}
