
import sangria.schema._
import sangria.execution.Executor
import sangria.macros.graphql

import scala.concurrent.ExecutionContext

case class Item(name: String, value: Int)

@main def main(): Unit =
  val ItemType = ObjectType("Item", fields[Unit, Item](
    Field("name", StringType, resolve = _.value.name),
    Field("value", IntType, resolve = _.value.value)
  ))

  val QueryType = ObjectType("Query", fields[Unit, Unit](
    Field("find", ItemType, resolve = _ => Item("item-1", 12))
  ))

  val schema = Schema(QueryType)

  val query = graphql"{ find { name value } }"

  given executionContext: ExecutionContext = ExecutionContext.global

  val result = Executor.execute(schema, query)

  result.foreach(r => println(r))
