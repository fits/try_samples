
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.`__`.*

fun main(args: Array<String>) {
    val conf = args[0]
    val start = args[1]
    val end = args[2]

    GraphFactory.open(conf).use { g ->
        g.tx().use {
            val p = g.traversal().V()
                    .has("oid", start)
                    .repeat(outE<Vertex>().`as`("e").inV())
                    .until(has<Vertex>("oid", end))
                    .where(select<Vertex, Vertex>("e").unfold<Vertex>().hasLabel("PERMIT"))
                    .path()

            p.forEach {
                println(it.objects())
            }
        }
    }
}
