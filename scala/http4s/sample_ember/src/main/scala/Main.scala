
import cats.effect.{IO, IOApp, ExitCode}
import com.comcast.ip4s.port
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.server.Router
import org.http4s.ember.server.EmberServerBuilder

object Main extends IOApp.Simple:
  val svc = HttpRoutes.of[IO] {
    case GET -> Root => Ok(s"home")
    case GET -> Root / "a" => Ok("a1")
  }

  val app = svc.orNotFound

  val server = EmberServerBuilder
    .default[IO]
    .withPort(port"8081")
    .withHttpApp(app)
    .build

  def run = server.useForever.as(ExitCode.Success)
