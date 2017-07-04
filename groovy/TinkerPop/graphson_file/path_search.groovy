@Grab('org.apache.tinkerpop:tinkergraph-gremlin:3.2.5')
@Grab('org.slf4j:slf4j-nop:1.7.25')
import org.apache.commons.configuration.BaseConfiguration
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__

def start = args[0]
def end = args[1]
def edgeLabel = args[2]
def dataFile = args[3]

def toStr = {
	"${it.label()}[${it.id()}]{${it.properties().join(', ')}}"
}

def config = new BaseConfiguration()

config.addProperty('gremlin.graph', 'org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph')
config.addProperty('gremlin.tinkergraph.graphFormat', 'graphson')
config.addProperty('gremlin.tinkergraph.graphLocation', dataFile)

GraphFactory.open(config).withAutoCloseable { g ->

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
