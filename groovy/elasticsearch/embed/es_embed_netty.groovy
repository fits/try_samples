@Grab('org.elasticsearch:elasticsearch:7.4.2')
@Grab('org.elasticsearch.plugin:transport-netty4-client:7.4.2')
@Grab('org.apache.logging.log4j:log4j-api:2.12.1')
@Grab('org.apache.logging.log4j:log4j-core:2.12.1')
import org.elasticsearch.env.Environment
import org.elasticsearch.node.Node
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.transport.Netty4Plugin

def setting = Settings.builder()
    .put(Environment.PATH_HOME_SETTING.getKey(), '.')
    .build()

def env = new Environment(setting, null)

new Node(env, [Netty4Plugin], true).withCloseable { node ->

    println node.getPluginsService().info().getPluginInfos()

    node.start()

    println 'started server ...'

    System.in.read()
}