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

def graph = JanusGraphFactory.open('cassandra.properties')

def toStr = {
	"${it.label()}[${it.id()}]{${it.properties().join(', ')}}"
}

def toStrEdge = {
	"${it.label()}[${it.id()}]{${it.properties().join(', ')}}, inV: ${it.inVertex()}, outV: ${it.outVertex()}"
}

graph.withAutoCloseable { g ->

	def ns = graph.traversal().V()

	println '---------- Vertex ----------'

	ns.each {
		println toStr(it)
	}

	def es = graph.traversal().E()

	println '---------- Edge ----------'

	es.each {
		println toStrEdge(it)
	}
}
