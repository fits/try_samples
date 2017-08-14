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

def conf = args[0]

def addNode = { g, type, id, name = id ->
    def res = g.addVertex(type)

    res.property('oid', id)
    res.property('name', name)

    res
}

def createData = { g -> 
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

GraphFactory.open(conf).withAutoCloseable { g ->
    g.tx().withAutoCloseable { tx ->
        createData(g)

        tx.commit()
    }
}
