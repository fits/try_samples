
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory

fun main(args: Array<String>) {
    val conf = args[0]

    GraphFactory.open(conf).use { g ->
        g.tx().use { tx ->
            createData(g)

            tx.commit()
        }
    }
}

fun createData(g: Graph) {
    val p = addNode(g, "Principals", "principals")

    val u1 = addNode(g, "User", "user1")
    val u2 = addNode(g, "User", "user2")
    val ad = addNode(g, "User", "admin")

    val g1 = addNode(g, "Group", "group1")

    listOf(u1, u2, ad, g1).forEach {
        it.addEdge("PART_OF", p)
    }

    u2.addEdge("PART_OF", g1)

    val r = addNode(g, "Resources", "resources")

    val s1 = addNode(g, "Service", "service1")

    val s2 = addNode(g, "Service", "service2")
    val s2o1 = addNode(g, "Operation", "service2.get", "get")
    val s2o2 = addNode(g, "Operation", "service2.post", "post")

    listOf(s2o1, s2o2).forEach {
        s2.addEdge("METHOD", it)
    }

    listOf(s1, s2).forEach {
        r.addEdge("RESOURCE", it)
    }

    u1.addEdge("PERMIT", s1)
    g1.addEdge("PERMIT", s2o2)
    ad.addEdge("PERMIT", r)
}

fun addNode(g: Graph, label: String, id: String, name: String = id): Vertex {
    val node = g.addVertex(label)

    node.property("oid", id)
    node.property("name", name)

    return node
}