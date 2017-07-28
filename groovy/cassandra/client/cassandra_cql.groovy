@Grapes([
	@Grab('com.datastax.cassandra:cassandra-driver-core:3.3.0'),
	@GrabExclude('com.github.jnr#jffi;1.2.10')
])
@Grab('org.slf4j:slf4j-nop:1.7.25')
import com.datastax.driver.core.Cluster

def cql = args[0]

Cluster.builder().addContactPoint('localhost').build().withCloseable { cluster ->
	cluster.connect().withCloseable { session ->
		def res = session.execute(cql)

		res.each {
			println it
		}
	}
}