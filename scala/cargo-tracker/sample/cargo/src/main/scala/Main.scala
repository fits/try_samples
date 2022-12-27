
import cats.effect.{ExitCode, IO, IOApp}
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.parser.decode
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
import sangria.validation.ValueCoercionViolation
import org.mongodb.scala.{MongoClient, MongoCollection}
import org.bson.Document

import scala.util.Properties
import scala.concurrent.{ExecutionContext, Future}
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

import models.*

case class GqlQuery(query: String, variables: Option[Map[String, String]])

type Revision = Long

case class StoredEvent(event: Event, createdAt: OffsetDateTime = OffsetDateTime.now())

case class StoredState(_id: String, rev: Revision, state: Cargo, events: List[StoredEvent]):
  def toDoc: Document = Document.parse(this.asJson.spaces2)

case class Store(col: MongoCollection[Document])(using ExecutionContext):
  import org.mongodb.scala.model.*

  def findById(trackingId: TrackingId): Future[Option[Cargo]] =
    loadState(trackingId).map(_.map(_.state))

  def create(cmd: CommandA[Event]): Future[Option[Cargo]] =
      Cargo.action(cmd).run(Cargo.Empty()) match {
        case Left(e) => Future.failed(e.toException)
        case Right((s, ev)) =>
          val id = Cargo.toId(s).orNull
          val doc = StoredState(id, 1, s, List(StoredEvent(ev, OffsetDateTime.now()))).toDoc

          col.insertOne(doc).head().map { _ =>
            Some(s)
          }
      }

  def update(trackingId: TrackingId, cmd: CommandA[Event]): Future[Option[Cargo]] =
    loadState(trackingId).flatMap { d =>
      val rev = d.map(_.rev).getOrElse(0)
      val state = d.map(_.state).getOrElse(Cargo.Empty())

      Cargo.action(cmd).run(state) match {
        case Left(e) => Future.failed(e.toException)
        case Right((s, ev)) =>
          val flt = Filters.and(Filters.eq("_id", trackingId), Filters.eq("rev", rev))

          val upd = Updates.combine(
            Updates.inc("rev", 1),
            Updates.set("state", Document.parse(s.asJson.spaces2)),
            Updates.push("events", Document.parse(StoredEvent(ev).asJson.spaces2))
          )

          val opts = FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)

          col.findOneAndUpdate(flt, upd, opts)
            .map(_ => s)
            .headOption()
      }
    }

  private def loadState(trackingId: TrackingId): Future[Option[StoredState]] =
    val flt = Filters.eq("_id", trackingId)

    col.find(flt)
      .first()
      .map(d => decode[StoredState](d.toJson).toTry.get)
      .headOption()

  extension (e: CommandError)
    def toException: Exception = e match {
      case CommandError.InvalidState() => Exception("invalid state")
      case CommandError.BlankId() => Exception("blank trackingId")
      case CommandError.PastDeadline() => Exception("passed deadline")
      case CommandError.NoChange(n) => Exception(s"no change: ${n}")
      case CommandError.EmptyLegs() => Exception("empty legs")
    }
end Store

case class LegInput(voyageNo: VoyageNo, loadLocation: UnLocode, loadTime: Date, unloadLocation: UnLocode, unloadTime: Date):
  def toLeg: Leg =
    Leg(voyageNo, LocationTime(loadLocation, loadTime), LocationTime(unloadLocation, unloadTime))

object App extends IOApp.Simple:
  val port: Port = Properties.envOrNone("SERVICE_PORT")
    .flatMap(Port.fromString)
    .orElse(Port.fromInt(8080))
    .get

  val mongoUri = Properties.envOrElse("MONGO_URI", "mongodb://localhost")
  val dbName = Properties.envOrElse("MONGO_DB", "cargo")
  val colName = Properties.envOrElse("MONGO_COLLECTION", "data")

  val mongo = MongoClient(mongoUri)

  val store = Store(
    mongo.getDatabase(dbName).getCollection(colName)
  )

  given EntityDecoder[IO, GqlQuery] = jsonOf
  given ExecutionContext = ExecutionContext.global

  case object DateCoercionViolation extends ValueCoercionViolation("")

  val parseDate = (d: String) =>
    try
      Right(OffsetDateTime.parse(d, DateTimeFormatter.ISO_DATE_TIME))
    catch
      case _ => Left(DateCoercionViolation)

  val DateType = ScalarType[Date](
    "Date",
    coerceOutput = (d, _) => d.format(DateTimeFormatter.ISO_DATE_TIME),
    coerceUserInput = {
      case s: String => parseDate(s)
      case _ => Left(DateCoercionViolation)
    },
    coerceInput = {
      case sangria.ast.StringValue(s, _, _, _, _) => parseDate(s)
      case _ => Left(DateCoercionViolation)
    }
  )

  val RouteSpecType = ObjectType("RouteSpec", fields[Unit, RouteSpec](
    Field("origin", IDType, resolve = _.value.origin),
    Field("destination", IDType, resolve = _.value.destination),
    Field("deadline", DateType, resolve = _.value.deadline)
  ))

  val LocationTimeType = ObjectType("LocationTime", fields[Unit, LocationTime](
    Field("location", IDType, resolve = _.value.location),
    Field("time", DateType, resolve = _.value.time)
  ))

  val LegType = ObjectType("Leg", fields[Unit, Leg](
    Field("voyageNo", IDType, resolve = _.value.voyageNo),
    Field("load", LocationTimeType, resolve = _.value.load),
    Field("unload", LocationTimeType, resolve = _.value.unload)
  ))

  val ItineraryType = ObjectType("Itinerary", fields[Unit, Itinerary](
    Field("legs", ListType(LegType), resolve = _.value.legs)
  ))

  val CargoInterfaceType = InterfaceType("Cargo", fields[Unit, HasRouteSpec](
    Field("trackingId", IDType, resolve = _.value.trackingId),
    Field("routeSpec", RouteSpecType, resolve = _.value.routeSpec)
  ))

  val RoutingInterfaceType = InterfaceType("Routing", fields[Unit, HasItinerary](
    Field("itinerary", ItineraryType, resolve = _.value.itinerary)
  ))

  val UnroutedCargoType = ObjectType(
    "UnroutedCargo",
    interfaces(CargoInterfaceType),
    fields[Unit, Cargo.Unrouted](
      Field("trackingId", IDType, resolve = _.value.trackingId),
      Field("routeSpec", RouteSpecType, resolve = _.value.routeSpec)
    )
  )

  val RoutedCargoType = ObjectType(
    "RoutedCargo",
    interfaces(CargoInterfaceType, RoutingInterfaceType),
    fields[Unit, Cargo.Routed](
      Field("trackingId", IDType, resolve = _.value.trackingId),
      Field("routeSpec", RouteSpecType, resolve = _.value.routeSpec),
      Field("itinerary", ItineraryType, resolve = _.value.itinerary)
    )
  )

  val MisroutedCargoType = ObjectType(
    "MisroutedCargo",
    interfaces(CargoInterfaceType, RoutingInterfaceType),
    fields[Unit, Cargo.Misrouted](
      Field("trackingId", IDType, resolve = _.value.trackingId),
      Field("routeSpec", RouteSpecType, resolve = _.value.routeSpec),
      Field("itinerary", ItineraryType, resolve = _.value.itinerary)
    )
  )

  val ClosedCargoType = ObjectType(
    "ClosedCargo",
    interfaces(CargoInterfaceType, RoutingInterfaceType),
    fields[Unit, Cargo.Closed](
      Field("trackingId", IDType, resolve = _.value.trackingId),
      Field("routeSpec", RouteSpecType, resolve = _.value.routeSpec),
      Field("itinerary", ItineraryType, resolve = _.value.itinerary)
    )
  )

  val LegInputType = InputObjectType[LegInput](
    "LegInput",
    List(
      InputField("voyageNo", IDType),
      InputField("loadLocation", IDType),
      InputField("loadTime", DateType),
      InputField("unloadLocation", IDType),
      InputField("unloadTime", DateType),
    )
  )

  val QueryType = ObjectType("Query", fields[Store, Unit](
    Field(
      "find",
      OptionType(CargoInterfaceType),
      arguments = Argument("trackingId", IDType) :: Nil,
      resolve = c => c.ctx.findById(c.args.arg[TrackingId]("trackingId"))
    ),
    Field(
      "isDestination",
      OptionType(BooleanType),
      arguments = List(Argument("trackingId", IDType), Argument("location", IDType)),
      resolve = c =>
        c.ctx.findById(c.args.arg[TrackingId]("trackingId"))
          .map { s =>
            val cmd = CommandA.IsDestination(c.args.arg[UnLocode]("location"))

            Cargo.action(cmd)
              .run(s.getOrElse(Cargo.Empty()))
              .map(_._2)
              .toOption
          }
    ),
    Field(
      "isOnRoute",
      OptionType(BooleanType),
      arguments = List(
          Argument("trackingId", IDType), Argument("location", IDType), Argument("voyageNo", OptionInputType(IDType))
      ),
      resolve = c =>
        c.ctx.findById(c.args.arg[TrackingId]("trackingId"))
          .map { s =>
            val cmd = CommandA.IsOnRoute(
              c.args.arg[UnLocode]("location"),
              c.args.argOpt[VoyageNo]("voyageNo")
            )

            Cargo.action(cmd)
              .run(s.getOrElse(Cargo.Empty()))
              .toOption
              .flatMap(_._2)
          }
    )
  ))

  val MutationType = ObjectType("Mutation", fields[Store, Unit](
    Field(
      "create",
      OptionType(CargoInterfaceType),
      arguments = List(Argument("origin", IDType), Argument("destination", IDType), Argument("deadline", DateType)),
      resolve = c =>
        c.ctx.create(CommandA.Create(
          UUID.randomUUID().toString,
          RouteSpec(
            c.args.arg[String]("origin"),
            c.args.arg[String]("destination"),
            c.args.arg[Date]("deadline")
          )
        ))
    ),
    Field(
      "assignToRoute",
      OptionType(CargoInterfaceType),
      arguments = List(Argument("trackingId", IDType), Argument("legs", ListInputType(LegInputType))),
      resolve = c =>
        val tid = c.args.arg[String]("trackingId")
        val legs = c.args.arg[Seq[LegInput]]("legs").map(_.toLeg)

        c.ctx.update(tid, CommandA.AssignRoute(Itinerary(legs.toList)))
    ),
    Field(
      "close",
      OptionType(CargoInterfaceType),
      arguments = Argument("trackingId", IDType) :: Nil,
      resolve = c =>
        val tid = c.args.arg[String]("trackingId")

        c.ctx.update(tid, CommandA.Close())
    ),
    Field(
      "changeDestination",
      OptionType(CargoInterfaceType),
      arguments = List(Argument("trackingId", IDType), Argument("destination", IDType)),
      resolve = c =>
        val tid = c.args.arg[String]("trackingId")
        val dest = c.args.arg[String]("destination")

        c.ctx.update(tid, CommandA.ChangeDestination(dest))
    ),
    Field(
      "changeDeadline",
      OptionType(CargoInterfaceType),
      arguments = List(Argument("trackingId", IDType), Argument("deadline", DateType)),
      resolve = c =>
        val tid = c.args.arg[String]("trackingId")
        val deadline = c.args.arg[Date]("deadline")

        c.ctx.update(tid, CommandA.ChangeDeadline(deadline))
    )
  ))

  val schema = Schema(
    QueryType,
    Some(MutationType),
    additionalTypes = List(UnroutedCargoType, RoutedCargoType, MisroutedCargoType, ClosedCargoType)
  )

  val svc = HttpRoutes.of[IO] {
    case req@POST -> Root =>
      val params = req.headers.get[`Content-Type`] match
        case Some(`Content-Type`(MediaType.application.json, _)) => req.as[GqlQuery]
        case _ => req.as[String].map(GqlQuery(_, None))

      params
        .flatMap { q =>
          val query = QueryParser.parse(q.query).get

          val vars = q.variables
            .map(InputUnmarshaller.mapVars)
            .getOrElse(InputUnmarshaller.emptyMapVars)

          val res = Executor.execute(schema, query, store, variables = vars)
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
