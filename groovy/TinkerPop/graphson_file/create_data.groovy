@Grab('org.apache.tinkerpop:tinkergraph-gremlin:3.2.5')
@Grab('org.slf4j:slf4j-nop:1.7.25')
import org.apache.commons.configuration.BaseConfiguration
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory

def addNode = { g, type, id, name = id ->
	def res = g.addVertex(type)

	res.property('oid', id)
	res.property('name', name)

	res
}

def tx = { g, proc ->
	if (g.features().graph().supportsTransactions()) {
		g.tx().withAutoCloseable { t ->
			try {
				proc()
				t.commit()
			} catch(e) {
				t.rollback()
				throw e
			}
		}
	}
	else {
		proc()
	}
}

def config = new BaseConfiguration()

config.addProperty('gremlin.graph', 'org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph')
config.addProperty('gremlin.tinkergraph.graphFormat', 'graphson')
config.addProperty('gremlin.tinkergraph.graphLocation', args[0])

GraphFactory.open(config).withAutoCloseable { g ->

	tx(g) {
		def p = addNode(g, 'Principals', 'principals')

		def u1 = addNode(g, 'User', 'user1')
		def u2 = addNode(g, 'User', 'user2')
		def ad = addNode(g, 'User', 'admin')

		def g1 = addNode(g, 'Group', 'group1')

		[u1, u2, ad, g1].each {
			it.addEdge('PART_OF', p)
		}

		u2.addEdge('PART_OF', g1)

		def r = addNode(g, 'Resources', 'resources')

		def s1 = addNode(g, 'Service', 'service1')

		def s2 = addNode(g, 'Service', 'service2')
		def s2o1 = addNode(g, 'Operation', 'service2.get', 'get')
		def s2o2 = addNode(g, 'Operation', 'service2.post', 'post')

		[s2o1, s2o2].each {
			s2.addEdge('METHOD', it)
		}

		[s1, s2].each {
			r.addEdge('RESOURCE', it)
		}

		u1.addEdge('PERMIT', s1)
		g1.addEdge('PERMIT', s2o2)
		ad.addEdge('PERMIT', r)
	}
}
