@Grapes([
    @Grab('org.apache.tinkerpop:neo4j-gremlin:3.2.5'),
    @Grab('org.neo4j:neo4j-tinkerpop-api-impl:0.6-3.2.2'),
    @Grab('org.slf4j:slf4j-nop:1.7.25'),
    @GrabExclude('org.codehaus.groovy:groovy-xml'),
    @GrabExclude('org.codehaus.groovy:groovy-swing'),
    @GrabExclude('org.codehaus.groovy:groovy-jsr223')
])
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__

def conf = args[0]
def start = args[1]
def end = args[2]

def toStr = {
    "${it.label()}[${it.id()}]{${it.properties().join(', ')}}"
}

GraphFactory.open(conf).withAutoCloseable { g ->
    g.tx().withAutoCloseable {
        def p = g.traversal().V()
            .has('oid', start)
            .repeat(__.out())
            .until(__.has('oid', end))
            .path()

        p.each {
            println it.objects().collect(toStr).join(' -> ')
        }
    }
}
