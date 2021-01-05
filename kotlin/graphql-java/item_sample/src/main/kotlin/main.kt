import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser

interface Item {
    val id: String
}

data class Variant(val category: String, val value: String)

data class SimpleItem(override val id: String) : Item
data class WithVariantsItem(override val id: String, val variants: List<Variant>) : Item

fun toVariants(param: Any?): List<Variant> =
    @Suppress("UNCHECKED_CAST")
    (param as? List<Map<String, String>>)?.map {
        Variant(it["category"]!!, it["value"]!!)
    } ?: emptyList()

fun main() {
    val schema = """
        interface Item {
            id: ID!
        }
        
        type SimpleItem implements Item {
            id: ID!
        }
        
        type WithVariantsItem implements Item {
            id: ID!
            variants: [Variant!]!
        }
        
        enum Category {
            Color
            Size
        }
        
        type Variant {
            category: Category!
            value: String!
        }
        
        input VariantInput {
            category: Category!
            value: String!
        }
        
        input CreateItem {
            id: ID!
            variants: [VariantInput!]
        }
        
        type Mutation {
            create(input: CreateItem!): Item
        }
        
        type Query {
            find(id: ID!): Item
        }
    """

    val store = hashMapOf<String, Item>()

    val parser = SchemaParser()
    val typeRegistry = parser.parse(schema)

    val wiring = RuntimeWiring
        .newRuntimeWiring()
        .type("Item") { bdr ->
            bdr.typeResolver { r ->
                when (r.getObject<Item>()) {
                    is WithVariantsItem -> r.schema.getObjectType("WithVariantsItem")
                    else -> r.schema.getObjectType("SimpleItem")
                }
            }
        }
        .type("Mutation") { bdr ->
            bdr.dataFetcher("create") { env ->
                val input = env.getArgument<Map<String, Any>>("input")

                val id = input["id"] as String

                if (store.containsKey(id))
                    null
                else {
                    val variants = toVariants(input["variants"])

                    val item =
                        if (variants.isEmpty())
                            SimpleItem(id)
                        else
                            WithVariantsItem(id, variants)

                    store[id] = item
                    item
                }
            }
        }
        .type("Query") { bdr ->
            bdr.dataFetcher("find") { env ->
                val id = env.getArgument<String>("id")
                store[id]
            }
        }
        .build()

    val gqlSchema = SchemaGenerator().makeExecutableSchema(typeRegistry, wiring)
    val gql = GraphQL.newGraphQL(gqlSchema).build()

    val q = """
        query FindItem(${'$'}id: ID!){
            find(id: ${'$'}id) {
                __typename
                id
                ... on WithVariantsItem {
                    variants {
                        category
                        value
                    }
                }
            }
        }
    """

    val ei = ExecutionInput
        .newExecutionInput(q)
        .variables(mapOf("id" to "item-0"))
        .build()

    val r = gql.execute(ei)
    println(r.getData<String>())

    val q1 = """
        mutation {
            create(input: {id: "item-1"}) {
                __typename
                id
            }
        }
    """

    val r1 = gql.execute(q1)
    println(r1.getData<String>())

    val ei1 = ExecutionInput
        .newExecutionInput(q)
        .variables(mapOf("id" to "item-1"))
        .build()

    val r11 = gql.execute(ei1)
    println(r11.getData<String>())

    val r12 = gql.execute(q1)
    println(r12.getData<String>())

    val q2 = """
		mutation {
			create(input: {id: "item-2", variants: [{category: Color, value: "White"}, {category: Size, value: "Free"}]}) {
				__typename
				id
			}
		}

    """

    val r2 = gql.execute(q2)
    println(r2.getData<String>())

    val ei2 = ExecutionInput
        .newExecutionInput(q)
        .variables(mapOf("id" to "item-2"))
        .build()

    val r21 = gql.execute(ei2)
    println(r21.getData<String>())
}
