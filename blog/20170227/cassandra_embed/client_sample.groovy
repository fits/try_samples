@Grapes([
	@Grab('com.datastax.cassandra:cassandra-driver-core:3.1.4'),
	@GrabExclude('io.netty#netty-handler;4.0.37'),
	@GrabExclude('com.github.jnr#jffi;1.2.10')
])
@Grab('io.netty:netty-all:4.0.44.Final')
@Grab('org.slf4j:slf4j-nop:1.7.23')
import com.datastax.driver.core.Cluster

Cluster.builder().addContactPoint('localhost').build().withCloseable { cluster ->
	cluster.connect('sample').withCloseable { session ->
		def res = session.execute('select * from data')

		res.each {
			println it
		}
	}
}
