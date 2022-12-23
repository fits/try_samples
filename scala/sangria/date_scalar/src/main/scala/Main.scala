
import sangria.schema.*
import sangria.execution.{Executor, ValidationError}
import sangria.macros.graphql
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

  val dateType = ScalarType[OffsetDateTime](
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

  val queryType = ObjectType("Query", fields[Unit, Unit](
    Field("now", dateType, resolve = _ => OffsetDateTime.now()),
    Field(
      "addDays", 
      dateType,
      arguments = List(Argument("date", dateType), Argument("n", IntType)),
      resolve = ctx => ctx.args.arg[OffsetDateTime]("date").plusDays(ctx.args.arg[Int]("n"))
    )
  ))

  val schema = Schema(queryType)

  given executionContext: ExecutionContext = ExecutionContext.global

  val r1 = Executor.execute(schema, graphql"{ now }")

  r1.foreach(println(_))

  val r2 = Executor.execute(schema, graphql"""{ addDays(date: "2022-12-26T15:10:00+09:00", n: 5) }""")

  r2.foreach(println(_))

  val r3 = Executor.execute(schema, graphql"""{ addDays(date: "2023-01-03T17:10:00Z", n: 2) }""")

  r3.foreach(println(_))

  val r4 = Executor.execute(schema, graphql"""{ addDays(date: "2023-01-10 06:10:00", n: -1) }""")

  r4.recover {
    case v: ValidationError => v.resolveError
  }.foreach(println(_))
