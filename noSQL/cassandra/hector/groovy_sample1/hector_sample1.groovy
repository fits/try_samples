package fits.sample

@Grapes([
	@Grab('me.prettyprint:hector-core:1.0-3'),
	@GrabExclude('commons-pool#commons-pool')
])
@Grab('org.slf4j:slf4j-jdk14:1.6.4')
import me.prettyprint.hector.api.factory.HFactory
import me.prettyprint.cassandra.serializers.StringSerializer

def cluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9160")
def keyspace = HFactory.createKeyspace('Sample', cluster)

def mutator = HFactory.createMutator(keyspace, StringSerializer.get())

mutator.addInsertion("key1", "Book", HFactory.createStringColumn("name", "test data"))

def res = mutator.execute()

println res

cluster.getConnectionManager().shutdown()
