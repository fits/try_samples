@Grab('org.elasticsearch:elasticsearch:5.2.0')
@Grab('org.elasticsearch.plugin:transport-netty4-client:5.2.0')
@Grab('org.apache.logging.log4j:log4j-api:2.8')
@Grab('org.apache.logging.log4j:log4j-core:2.8')
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.env.Environment
import org.elasticsearch.node.Node
import org.elasticsearch.transport.Netty4Plugin

def setting = Settings.builder()
	.put('path.home', '.')
	.build()

def env = new Environment(setting)

new Node(env, [Netty4Plugin]).withCloseable { node ->

	node.start()

	println 'started server ...'

	System.in.read()
}