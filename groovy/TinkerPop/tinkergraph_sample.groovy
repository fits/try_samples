// groovy 2.5.0-beta-1
@Grab('org.apache.tinkerpop:tinkergraph-gremlin:3.2.5')
@Grab('org.slf4j:slf4j-nop:1.7.25')
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__
import static org.apache.tinkerpop.gremlin.process.traversal.P.*

def start = args[0]
def end = args[1]

def addNode = { g, type, id, name = id ->
	def res = g.addVertex(type)

	res.property('oid', id)
	res.property('name', name)

	res
}

def createData = { g ->
	def principal = addNode(g, 'Principals', 'principals')

	def group = addNode(g, 'Group', 'groupA')
	group.addEdge('PART_OF', principal)

	def user1 = addNode(g, 'User', 'user1')
	def user2 = addNode(g, 'User', 'user2')
	def user3 = addNode(g, 'User', 'user3')

	user1.addEdge('PART_OF', group)

	[user1, user2, user3].each { it.addEdge('PART_OF', principal) }

	def resources = addNode(g, 'Resources', 'resources')

	def service1 = addNode(g, 'Service', 'service1')
	def opr11 = addNode(g, 'Operation', 'service1.opr1', 'opr1')
	def opr12 = addNode(g, 'Operation', 'service1.opr2', 'opr2')

	[opr11, opr12].each { service1.addEdge('METHOD', it) }

	def service2 = addNode(g, 'Service', 'service2')
	def opr21 = addNode(g, 'Operation', 'service2.get', 'get')
	def opr22 = addNode(g, 'Operation', 'service2.post', 'post')

	[opr21, opr22].each { service2.addEdge('METHOD', it) }

	[service1, service2].each { resources.addEdge('RESOURCE', it) }

	group.addEdge('PERMIT', opr11, 'action', 'invoke')
	user2.addEdge('PERMIT', service2, 'action', 'invoke')
}

def toStr = {
	"${it.label()}(${it.id()}){${it.property('oid')}}"
}

TinkerFactory.createModern().withAutoCloseable { g ->

	createData(g)

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
