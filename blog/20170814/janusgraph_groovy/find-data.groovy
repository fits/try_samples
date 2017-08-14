@Grapes([
    @Grab('org.janusgraph:janusgraph-cassandra:0.1.1'),
    @Grab('org.slf4j:slf4j-simple:1.7.25'),
    @GrabExclude('xml-apis#xml-apis;1.3.04'),
    @GrabExclude('com.github.jeremyh#jBCrypt;jbcrypt-0.4'),
    @GrabExclude('org.slf4j#slf4j-log4j12'),
    @GrabExclude('ch.qos.logback#logback-classic'),
    @GrabExclude('org.codehaus.groovy:groovy-swing'),
    @GrabExclude('org.codehaus.groovy:groovy-xml'),
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
            .repeat(__.outE().as('e').inV())
            .until(__.has('oid', end))
            .where(__.select('e').unfold().hasLabel('PERMIT'))
            .path()

        p.each {
            println it.objects().collect(toStr).join(' -> ')
        }
    }
}
