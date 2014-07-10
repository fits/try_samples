@Grab('org.apache.solr:solr-core:4.9.0')
@Grab('org.slf4j:slf4j-nop:1.7.7')
import org.apache.solr.core.CoreContainer
import org.apache.solr.request.LocalSolrQueryRequest
import org.apache.solr.response.SolrQueryResponse
import org.apache.solr.response.BinaryResponseWriter
import org.apache.solr.common.util.JavaBinCodec

def cores = new CoreContainer(args[0])
cores.load()

def core = cores.getCore(args[1])
def handler = core.getRequestHandler('/query')

def req = new LocalSolrQueryRequest(core, [
	q: ['*:*'] as String[]
])

def res = new SolrQueryResponse()

core.execute(handler, req, res)

def writer = new ByteArrayOutputStream()

def rw = new BinaryResponseWriter()
rw.write(writer, req, res)

def resolver = new BinaryResponseWriter.Resolver(req, res.getReturnFields())
def bais = new ByteArrayInputStream(writer.toByteArray())

def result = new JavaBinCodec(resolver).unmarshal(bais)

println result.class

result.response.each {
	println it
}

core.close()
cores.shutdown()
