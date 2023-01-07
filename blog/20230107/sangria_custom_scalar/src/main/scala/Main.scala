
import sangria.schema.*
import sangria.execution.{Executor, ErrorWithResolver}
import sangria.macros.graphql
import sangria.marshalling.InputUnmarshaller
import sangria.marshalling.circe.*
import sangria.validation.ValueCoercionViolation

import scala.concurrent.ExecutionContext
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@main def main(): Unit =
  case object DateCoercionViolation extends ValueCoercionViolation("invalid date")

  val parseDate = (d: String) =>
    try
      Right(OffsetDateTime.parse(d, DateTimeFormatter.ISO_DATE_TIME))
    catch
      case _ => Left(DateCoercionViolation)

  val DateType = ScalarType[OffsetDateTime](
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

  val QueryType = ObjectType("Query", fields[Unit, Unit](
    Field("now", DateType, resolve = _ => OffsetDateTime.now()),
    Field(
      "addDays", 
      DateType,
      arguments = List(Argument("date", DateType), Argument("n", IntType)),
      resolve = ctx => ctx.args.arg[OffsetDateTime]("date").plusDays(ctx.args.arg[Int]("n"))
    )
  ))

  val schema = Schema(QueryType)

  given executionContext: ExecutionContext = ExecutionContext.global

  val r1 = Executor.execute(schema, graphql"{ now }")

  r1.foreach(println(_))

  val r2 = Executor.execute(schema, graphql"""{ addDays(date: "2023-02-10T15:10:00+09:00", n: 2) }""")

  r2.foreach(println(_))

  val r3 = Executor.execute(schema, graphql"""{ addDays(date: "2023-03-10T17:10:00Z", n: 3) }""")

  r3.foreach(println(_))

  val r4 = Executor.execute(schema, graphql"""{ addDays(date: "2023-04-10 06:10:00", n: 4) }""")

  r4.recover {
    case e: ErrorWithResolver => e.resolveError
  }.foreach(println(_))

  val q = graphql"""
    query ($$d: Date!) {
      addDays(date: $$d, n: 5)      
    }
  """

  val vs = InputUnmarshaller.mapVars("d" -> "2023-05-10T17:30:00+09:00")

  val r5 = Executor.execute(schema, q, variables = vs)

  r5.foreach(println(_))
