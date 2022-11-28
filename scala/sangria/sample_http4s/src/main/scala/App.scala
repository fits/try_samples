import cats.effect.{ExitCode, IO, IOApp}
import io.circe.generic.auto.*
import com.comcast.ip4s.Port
import org.http4s.{EntityDecoder, HttpRoutes, MediaType}
import org.http4s.circe.jsonOf
import org.http4s.headers.`Content-Type`
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import sangria.schema.*
import sangria.execution.{ErrorWithResolver, Executor}
import sangria.parser.QueryParser
import sangria.marshalling.InputUnmarshaller
import sangria.marshalling.circe.*

import scala.util.Properties
import scala.concurrent.ExecutionContext

case class GqlQuery(query: String, variables: Option[Map[String, String]])
case class Item(name: String, value: Int)

object App extends IOApp.Simple:
  val port: Port = Properties.envOrNone("SERVICE_PORT")
    .flatMap(Port.fromString)
    .orElse(Port.fromInt(8080))
    .get

  given EntityDecoder[IO, GqlQuery] = jsonOf
  given ExecutionContext = ExecutionContext.global

  val ItemType = ObjectType("Item", fields[Unit, Item](
    Field("name", StringType, resolve = _.value.name),
    Field("value", IntType, resolve = _.value.value)
  ))

  val QueryType = ObjectType("Query", fields[Unit, Unit](
    Field(
      "find",
      ItemType,
      arguments = List(Argument("name", StringType)),
      resolve = ctx => Item(ctx.args.arg("name"), 12)
    )
  ))

  val schema = Schema(QueryType)

  val svc = HttpRoutes.of[IO] {
    case req @ POST -> Root =>
      val params = req.headers.get[`Content-Type`] match
        case Some(`Content-Type`(MediaType.application.json, _)) => req.as[GqlQuery]
        case _ => req.as[String].map(GqlQuery(_, None))

      params
        .flatMap { q =>
          val query = QueryParser.parse(q.query).get

          val vars = q.variables
            .map(InputUnmarshaller.mapVars)
            .getOrElse(InputUnmarshaller.emptyMapVars)

          val res = Executor.execute(schema, query, variables = vars)
            .recover {
              case e: ErrorWithResolver => e.resolveError
            }
            .map(_.spaces2)

          IO.fromFuture(IO(res))
        }
        .flatMap(Ok(_))
  }

  val app = svc.orNotFound

  val server = EmberServerBuilder
    .default[IO]
    .withPort(port)
    .withHttpApp(app)
    .build

  override def run: IO[Unit] = server.useForever.as(ExitCode.Success)
