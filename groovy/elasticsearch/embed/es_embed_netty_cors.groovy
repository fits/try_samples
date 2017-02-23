@Grab('org.elasticsearch:elasticsearch:5.2.1')
@Grab('org.elasticsearch.plugin:transport-netty4-client:5.2.1')
@Grab('org.apache.logging.log4j:log4j-api:2.8')
@Grab('org.apache.logging.log4j:log4j-core:2.8')
import org.elasticsearch.env.Environment
import org.elasticsearch.node.Node
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.transport.Netty4Plugin

def setting = Settings.builder()
	.put('path.home', '.')
	.put('http.cors.enabled', 'true')
	.put('http.cors.allow-origin', '/https?:\\/\\/localhost(:[0-9]+)?/')
	.build()

def env = new Environment(setting)

def node = new Node(env, [Netty4Plugin])

println node.getPluginsService().info().getPluginInfos()

node.start()

println 'started server ...'

System.in.read()