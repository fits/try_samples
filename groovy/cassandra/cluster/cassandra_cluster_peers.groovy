@Grapes([
    @Grab('com.datastax.cassandra:cassandra-driver-core:3.2.0'),
    @GrabExclude('io.netty#netty-handler;4.0.37'),
    @GrabExclude('com.github.jnr#jffi;1.2.10')
])
@Grab('io.netty:netty-all:4.0.44.Final')
@Grab('org.slf4j:slf4j-nop:1.7.23')
import com.datastax.driver.core.Cluster

def port = args.length > 0 ? args[0] as int : 9042
def host = args.length > 1 ? args[1] : '127.0.0.1'

Cluster.builder()
	.addContactPoint(host)
	.withPort(port)
	.build().withCloseable { cluster ->

	cluster.connect().withCloseable { session ->

		println '----- local -----'

		session.execute('select key, bootstrapped, broadcast_address, cluster_name, data_center, host_id, gossip_generation, partitioner, rack, rpc_address from system.local').each { println it }

		println '----- peers -----'

		session.execute('select peer, data_center, host_id, preferred_ip, rack, rpc_address from system.peers').each { println it }

	}
}