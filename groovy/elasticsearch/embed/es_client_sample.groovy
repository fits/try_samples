@Grab('org.elasticsearch.client:transport:5.2.0')
@Grab('org.apache.logging.log4j:log4j-api:2.8')
@Grab('org.apache.logging.log4j:log4j-core:2.8')
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress

def addr = new InetSocketTransportAddress(InetAddress.getLoopbackAddress(), 9300)

new PreBuiltTransportClient(Settings.EMPTY)
	.addTransportAddress(addr)
	.withCloseable { client ->

	def r1 = client.prepareIndex('b1', 'prod')
				.setSource('time', System.currentTimeMillis())
				.execute()
				.get()

	println r1

	sleep(3000)

	println '-----'

	def r2 = client.prepareSearch('b1')
				.setTypes('prod')
				.execute()
				.get()

	println r2
}
