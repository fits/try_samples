@Grapes([
	@Grab('org.janusgraph:janusgraph-cassandra:0.1.1'),
	@Grab('org.slf4j:slf4j-nop:1.7.25'),
	@GrabExclude('xml-apis#xml-apis;1.3.04'),
	@GrabExclude('com.github.jeremyh#jBCrypt;jbcrypt-0.4'),
	@GrabExclude('org.slf4j#slf4j-log4j12'),
	@GrabExclude('ch.qos.logback#logback-classic'),
	@GrabExclude('org.codehaus.groovy:groovy-swing'),
	@GrabExclude('org.codehaus.groovy:groovy-xml'),
	@GrabExclude('org.codehaus.groovy:groovy-jsr223')
])
import org.janusgraph.core.JanusGraphFactory
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__

def start = args[0]
def end = args[1]
def edgeLabel = args[2]

def graph = JanusGraphFactory.open('cassandra.properties')

def toStr = {
	"${it.label()}[${it.id()}]{${it.properties().join(', ')}}"
}

graph.withAutoCloseable { g ->

	def p = g.traversal().V()
		.has('oid', start)
		.repeat(__.outE().as('e').inV())
		.until(__.has('oid', end))
		.where(__.select('e').unfold().hasLabel(edgeLabel))
		.path()

	p.each {
		println it.objects().collect(toStr).join(' -> ')
	}
}
