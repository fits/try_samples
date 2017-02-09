@Grab('io.searchbox:jest:2.0.4')
@Grab('org.slf4j:slf4j-nop:1.7.22')
import io.searchbox.core.Index
import io.searchbox.client.JestClientFactory
import io.searchbox.client.config.HttpClientConfig
import io.searchbox.core.Search

def index = args[0]
def type = args[1]

def factory = new JestClientFactory()

def config = new HttpClientConfig.Builder('http://localhost:9200').build()

factory.setHttpClientConfig(config)

def client = factory.object

try {
	def params = [ time: System.currentTimeMillis() ]

	def r1 = client.execute(
		new Index.Builder(params).index(index).type(type).build()
	)

	println r1.jsonString

	sleep(1000)

	println '-----'

	def r2 = client.execute(
		new Search.Builder('').addIndex(index).addType(type).build()
	)

	println r2.jsonString

} finally {
	client.shutdownClient()
}
