@Grab('org.elasticsearch:elasticsearch:5.2.0')
@Grab('org.apache.logging.log4j:log4j-api:2.8')
@Grab('org.apache.logging.log4j:log4j-core:2.8')
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.node.Node

def index = args[0]
def type = args[1]

def setting = Settings.builder()
	.put('path.home', '.')
	.put('transport.type', 'local')
	.put('http.enabled', 'false')
	.build()

new Node(setting).withCloseable { node ->

	node.start()

	node.client().withCloseable { client ->

		def r1 = client.prepareIndex(index, type)
					.setSource('time', System.currentTimeMillis())
					.execute()
					.get()

		println r1

		sleep(1000)

		println '-----'

		def r2 = client.prepareSearch(index)
					.setTypes(type)
					.execute()
					.get()

		println r2
	}
}
