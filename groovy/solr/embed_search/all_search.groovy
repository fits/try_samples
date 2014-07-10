@Grab('org.apache.solr:solr-core:4.9.0')
@Grab('org.slf4j:slf4j-nop:1.7.7')
import org.apache.solr.core.CoreContainer
import org.apache.solr.request.LocalSolrQueryRequest
import org.apache.solr.response.SolrQueryResponse
import org.apache.solr.response.JSONResponseWriter

def cores = new CoreContainer(args[0])
cores.load()

def core = cores.getCore(args[1])
def handler = core.getRequestHandler('/query')

def req = new LocalSolrQueryRequest(core, [
	q: ['*:*'] as String[]
])

def res = new SolrQueryResponse()

handler.handleRequest(req, res)

def writer = new StringWriter()

def json = new JSONResponseWriter()
json.write(writer, req, res)

println writer.toString()

core.close()
cores.shutdown()
