
import sangria.schema._
import sangria.execution.Executor
import sangria.macros.graphql

import scala.concurrent.ExecutionContext

case class Item(name: String, value: Int)

def sample1(): Unit =
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
end sample1

def sample2(): Unit =
  val ast =
    graphql"""
      type Item {
        name: String!
        value: Int!
      }

      type Query {
        find: Item!
      }
    """

  import sangria.schema.AstSchemaBuilder._

  val builder = AstSchemaBuilder.resolverBased(
    FieldResolver(
      {
        case (TypeName("Query"), FieldName("find")) => _ => Item("item-2", 34)
        case (TypeName("Item"), FieldName("name")) => _.asInstanceOf[Context[_, Item]].value.name
        case (TypeName("Item"), FieldName("value")) => _.asInstanceOf[Context[_, Item]].value.value
      },
      PartialFunction.empty
    )
  )

  val schema = Schema.buildFromAst(ast, builder)

  val query = graphql"{ find { name value } }"

  given executionContext: ExecutionContext = ExecutionContext.global

  val result = Executor.execute(schema, query)

  result.foreach(r => println(r))
end sample2

def sample2b(): Unit =
  val ast =
    graphql"""
      type Item {
        name: String!
        value: Int!
      }

      type Query {
        find: Item!
      }
    """

  import sangria.schema.AstSchemaBuilder._

  val builder = AstSchemaBuilder.resolverBased(
    FieldResolver.map(
      "Query" -> Map(
        "find" -> (_ => Item("item-2b", 32)),
      ),
      "Item" -> Map(
        "name" -> (_.asInstanceOf[Context[_, Item]].value.name),
        "value" -> (_.asInstanceOf[Context[_, Item]].value.value)
      )
    )
  )

  val schema = Schema.buildFromAst(ast, builder)

  val query = graphql"{ find { name value } }"

  given executionContext: ExecutionContext = ExecutionContext.global

  val result = Executor.execute(schema, query)

  result.foreach(r => println(r))
end sample2b

@main def main(): Unit =
  sample1()
  sample2()
  sample2b()
