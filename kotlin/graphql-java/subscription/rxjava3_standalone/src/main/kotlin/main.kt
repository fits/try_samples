
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.UnicastProcessor
import io.reactivex.rxjava3.subscribers.TestSubscriber
import org.reactivestreams.Publisher
import java.util.concurrent.CopyOnWriteArrayList

data class Item(val id: String)

fun main() {
    val schema = """
        type Item {
            id: ID!
        }
        
        input CreateItem {
            id: ID!
        }
        
        type Mutation {
            create(input: CreateItem!): Item
        }

        type Subscription {
            created: Item
        }
        
        type Query {
            none: Item
        }
    """

    val processors = CopyOnWriteArrayList<UnicastProcessor<Item>>()

    val parser = SchemaParser()
    val typeRegistry = parser.parse(schema)

    val wiring = RuntimeWiring
        .newRuntimeWiring()
        .type("Mutation") { bdr ->
            bdr.dataFetcher("create") { env ->
                val input = env.getArgument<Map<String, Any>>("input")
                val item = Item(input["id"] as String)

                processors.forEach {
                    it.onNext(item)
                }

                item
            }
        }
        .type("Subscription") { bdr ->
            bdr.dataFetcher("created") {
                val p = UnicastProcessor.create<Item>()

                processors.add(p)

                p
            }
        }
        .type("Query") { bdr ->
            bdr.dataFetcher("none") {
                null
            }
        }
        .build()

    val gqlSchema = SchemaGenerator().makeExecutableSchema(typeRegistry, wiring)

    val gql = GraphQL.newGraphQL(gqlSchema).build()

    val s = """
        subscription {
            created {
                id
            }
        }
    """

    val stream = gql.execute(s).getData<Publisher<ExecutionResult>>()
    val testSubsc = TestSubscriber.create<String>()

    Flowable.fromPublisher(stream).map {
        val d = it.getData<Map<String, Map<String, String>>>()

        println("*** received: $d")

        d?.get("created")?.get("id") ?: ""

    }.subscribe(testSubsc)

    val q1 = """
        mutation {
            create(input: {id: "item-1"}) {
                id
            }
        }
    """

    val r1 = gql.execute(q1)
    println(r1.getData<String>())

    val q2 = """
        mutation {
            create(input: {id: "item-2"}) {
                id
            }
        }
    """

    val r2 = gql.execute(q2)
    println(r2.getData<String>())

    testSubsc.assertValues("item-1", "item-2")
}