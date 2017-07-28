@Grapes([
	@Grab('com.datastax.cassandra:cassandra-driver-core:3.3.0'),
	@GrabExclude('com.github.jnr#jffi;1.2.10')
])
@Grab('org.slf4j:slf4j-nop:1.7.25')
import com.datastax.driver.core.Cluster

Cluster.builder().addContactPoint('localhost').build().withCloseable { cluster ->
	cluster.metadata.keyspaces.each {
		println "----- ${it.name} -----"

		println '[table]'
		it.tables.each {
			println "  ${it.name}, ${it.indexes*.name}"
		}

		println '[aggregates]'
		it.aggregates.each { println "  ${it.name}" }

		println '[functions]'
		it.functions.each { println "  $it.name}" }
	}
}