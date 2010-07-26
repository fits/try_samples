
import org.neo4j.graphdb.DynamicRelationshipType
import org.neo4j.kernel.EmbeddedGraphDatabase

def db = new EmbeddedGraphDatabase("sample-store1")

def tx = db.beginTx()

try {
	def n1 = db.createNode()
	n1.setProperty("name", "Tester1")

	println("n1 id = ${n1.id}")

	def n2 = db.createNode()
	n2.setProperty("name", "test2")

	println("n2 id = ${n2.id}")

	def know = DynamicRelationshipType.withName("knows")

	n1.createRelationshipTo(n2, know)

	tx.success()
} catch (ex) {
	ex.printStackTrace()
} finally {
	tx.finish()
}

println("shutdown db ...")
db.shutdown()

