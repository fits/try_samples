@Grab('org.apache.cassandra:cassandra-all:3.10')
import org.apache.cassandra.service.CassandraDaemon

def conf = 'embed_cassandra.yaml'
def dir = new File(args[0])

if (!dir.exists()) {
	dir.mkdirs()
}

System.setProperty('cassandra.config', conf)
System.setProperty('cassandra-foreground', 'true')
System.setProperty('cassandra.storagedir', dir.absolutePath)

def cassandra = new CassandraDaemon(true)
cassandra.activate()

System.in.read()

cassandra.deactivate()

