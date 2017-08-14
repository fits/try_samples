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
import org.janusgraph.core.JanusGraphFactory
import org.janusgraph.core.schema.SchemaAction
import org.apache.tinkerpop.gremlin.structure.Vertex

def conf = args[0]

def graph = JanusGraphFactory.open(conf)

graph.withAutoCloseable { g ->

    def manage = g.openManagement()

    def oidKey = manage.getPropertyKey('oid')

    if (manage.getGraphIndex('oidIndex') == null) {
        manage.buildIndex('oidIndex', Vertex).addKey(oidKey).buildCompositeIndex()

        manage.commit()

        manage.awaitGraphIndexStatus(g, 'oidIndex').call()
    }

    manage = g.openManagement()

    manage.updateIndex(manage.getGraphIndex('oidIndex'), SchemaAction.REINDEX).get()

    manage.commit()
}
