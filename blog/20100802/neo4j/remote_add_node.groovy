import org.neo4j.remote.RemoteGraphDatabase
import org.neo4j.graphdb.DynamicRelationshipType

def db = new RemoteGraphDatabase("rmi://localhost/remote-test")

def tx = db.beginTx()

try {
	def know = DynamicRelationshipType.withName("knows")

	def n1 = db.createNode()
	n1.setProperty("name", "tester1")

	println("tester1 id = ${n1.id}")

	n11 = db.createNode()
	n11.setProperty("name", "tester1-1")
	n1.createRelationshipTo(n11, know)

	n111 = db.createNode()
	n111.setProperty("name", "tester1-1-1")
	n11.createRelationshipTo(n111, know)

	n1111 = db.createNode()
	n1111.setProperty("name", "tester1-1-1-1")
	n111.createRelationshipTo(n1111, know)

	n1112 = db.createNode()
	n1112.setProperty("name", "tester1-1-1-2")
	n111.createRelationshipTo(n1112, know)

	n12 = db.createNode()
	n12.setProperty("a", "tester1-2")
	n1.createRelationshipTo(n12, know)

	tx.success()
} catch (ex) {
	ex.printStackTrace()
} finally {
	tx.finish()
}

println("shutdown db ...")
db.shutdown()
