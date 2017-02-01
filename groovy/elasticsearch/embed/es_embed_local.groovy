@Grab('org.elasticsearch:elasticsearch:5.2.0')
@Grab('org.apache.logging.log4j:log4j-api:2.8')
@Grab('org.apache.logging.log4j:log4j-core:2.8')
import org.elasticsearch.env.Environment
import org.elasticsearch.node.Node
import org.elasticsearch.common.settings.Settings

def setting = Settings.builder()
	.put('path.home', '.')
	.put('transport.type', 'local')
	.put('http.enabled', 'false')
	.build()

def env = new Environment(setting)

def node = new Node(env)

node.start()

def client = node.client()

def r1 = client.prepareIndex('b1', 'prod')
			.setSource('time', System.currentTimeMillis())
			.execute()
			.get()

println r1

sleep(3000)

println '-----'

def r2 = client.prepareSearch('b1')
			.execute()
			.get()

println r2

client.close()

