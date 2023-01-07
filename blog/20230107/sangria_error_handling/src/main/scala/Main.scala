
import sangria.schema.*
import sangria.execution.{Executor, ExceptionHandler, HandledException, ErrorWithResolver}
import sangria.macros.graphql
import sangria.marshalling.circe.*

import scala.concurrent.ExecutionContext

@main def main(): Unit =
  val QueryType = ObjectType("Query", fields[Unit, Unit](
    Field(
      "sample", 
      StringType, 
      arguments = Argument("input", StringType) :: Nil,
      resolve = c =>
        val input = c.args.arg[String]("input")

        if input.isBlank then
          throw Exception("input is blank")
        else
          s"ok-${input}"
    )
  ))

  val schema = Schema(QueryType)

  val q = graphql"""{ sample(input: "") }"""

  given ExecutionContext = ExecutionContext.global

  val r1 = Executor.execute(schema, q)

  println(s"* r1 = ${r1}")

  r1.foreach(r => println(s"r1 result = ${r}"))

  val exceptionHandler = ExceptionHandler {
    case (_, e) => {
      println(s"*** Error Handling: ${e}")
      HandledException(e.getMessage)
    }
  }

  val r2 = Executor.execute(schema, q, exceptionHandler = exceptionHandler)
  r2.foreach(r => println(s"r2 result = ${r}"))

  val q2 = graphql"{ sample }"

  val r3 = Executor.execute(schema, q2, exceptionHandler = exceptionHandler)

  println(s"* r3 = ${r3}")

  r3.foreach(r => println(s"r3 result1 = ${r}"))

  r3.recover {
    case e: ErrorWithResolver => e.resolveError
  }.foreach(r => println(s"r3 result2 = ${r}"))

  val r4 = Executor.execute(schema, q2, exceptionHandler = exceptionHandler, queryValidator = sangria.validation.QueryValidator.empty)

  println(s"* r4 = ${r4}")

  r4.foreach(r => println(s"r4 result = ${r}"))