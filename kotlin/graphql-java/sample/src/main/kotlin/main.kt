import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser

data class Item(val id: String, val value: Int)

fun main() {
    val schema = """
        type Item {
            id: ID!
            value: Int!
        }
        
        type Query {
            find(id: ID!): Item
        }
    """

    val parser = SchemaParser()
    val typeRegistry = parser.parse(schema)

    val wiring = RuntimeWiring
        .newRuntimeWiring()
        .type("Query") { bld ->
            bld.dataFetcher("find") { env ->
                val id = env.getArgument<String>("id")
                Item(id, 123)
            }
        }
        .build()

    val gqlSchema = SchemaGenerator().makeExecutableSchema(typeRegistry, wiring)

    val gql = GraphQL.newGraphQL(gqlSchema).build()

    val q = """
        {
            find(id: "item-1") {
                id
                value
            }
        }
    """

    val res = gql.execute(q)

    println(res.getData<String>())
}