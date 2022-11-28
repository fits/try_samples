
import sangria.schema._
import sangria.execution.{Executor, ExceptionHandler, HandledException, ValidationError, ErrorWithResolver}
import sangria.macros.graphql
import sangria.marshalling.circe._
import sangria.validation.QueryValidator

import scala.concurrent.{ExecutionContext, Future}

case class Item(name: String, value: Int)

@main def main(): Unit =
  val ItemType = ObjectType("Item", fields[Unit, Item](
    Field("name", StringType, resolve = _.value.name),
    Field("value", IntType, resolve = _.value.value)
  ))

  val QueryType = ObjectType("Query", fields[Unit, Unit](
    Field("find", ItemType, resolve = _ => Future.failed(Exception("test error")))
  ))

  val schema = Schema(QueryType)

  val q1 = graphql"{ find { name value } }"

  given executionContext: ExecutionContext = ExecutionContext.global

  val r1 = Executor.execute(schema, q1)

  r1.foreach(r => println(s"r1 result = ${r.spaces2}"))

  val exceptionHandler = ExceptionHandler {
    case (_, e) => {
      println(s"*** Error Handling: ${e}")
      HandledException(e.getMessage)
    }
  }

  val r2 = Executor.execute(schema, q1, exceptionHandler = exceptionHandler)

  r2.foreach(r => println(s"r2 result = ${r.spaces2}"))

  val q2 = graphql"{ find { id } }"

  val r3 = Executor.execute(schema, q2, queryValidator = QueryValidator.empty)

  println(s"* r3 = ${r3}")

  r3.foreach(r => println(s"r3 result = ${r.spaces2}"))

  val r4 = Executor.execute(schema, q2)

  println(s"* r4 = ${r4}")

  r4.recover {
    case v: ValidationError => v.resolveError
  }.foreach(r => println(s"r4 result = ${r.spaces2}"))

  r4.recover {
    case v: ErrorWithResolver => v.resolveError
  }.foreach(r => println(s"r4 result ErrorWithResolver = ${r.spaces2}"))
