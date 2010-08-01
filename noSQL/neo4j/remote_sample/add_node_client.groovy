
import org.neo4j.remote.RemoteGraphDatabase
import org.neo4j.graphdb.DynamicRelationshipType

def db = new RemoteGraphDatabase("rmi://localhost/remote-test")

def tx = db.beginTx()

try {
	def know = DynamicRelationshipType.withName("knows")

	def n1 = db.createNode()
	n1.setProperty("name", "Tester1")

	println("n1 id = ${n1.id}")

	n11 = db.createNode()
	n11.setProperty("name", "tester1-1")
	n1.createRelationshipTo(n11, know)

	tx.success()
} catch (ex) {
	ex.printStackTrace()
} finally {
	tx.finish()
}

println("shutdown db ...")
db.shutdown()
