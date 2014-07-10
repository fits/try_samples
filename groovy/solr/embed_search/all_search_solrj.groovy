@Grab('org.apache.solr:solr-core:4.9.0')
@Grab('org.slf4j:slf4j-nop:1.7.7')
import org.apache.solr.core.CoreContainer
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer

def cores = new CoreContainer(args[0])
cores.load()

def server = new EmbeddedSolrServer(cores, args[1])

def q = new SolrQuery(query: '*:*')

def res = server.query(q)

res.results.each {
	println it
}

server.shutdown()
