
import org.neo4j.kernel.EmbeddedGraphDatabase

def db = new EmbeddedGraphDatabase("sample-store1")

def tx = db.beginTx()

try {
	db.allNodes.each {
		if (it.hasProperty("name")) {
			println it.id + " - " + it.getProperty("name")
		}
		else {
			println it.id
		}
	}
} finally {
	tx.finish()
}

println("shutdown db ...")
db.shutdown()

