
import org.neo4j.remote.RemoteGraphDatabase
import org.neo4j.graphdb.*

def id = (args.length > 0)? args[0].toInteger(): 1

def db = new RemoteGraphDatabase("rmi://localhost/remote-test")

def tx = db.beginTx()

//Node の name プロパティの値を出力
def printName = {n -> 
	if (n.hasProperty("name")) {
		println n.id + " - " + n.getProperty("name")
	}
	else {
		println n.id + " - ** no name **"
	}
}

try {
	//id=1 の Node 取得
	def n = db.getNodeById(id)

	//id=1 の Node の名前出力
	printName(n)

	//Node の全Relationship取得
	n.relationships.each {
		//End Node の名前出力
		printName(it.endNode)
	}

	println "--------------------"

	def know = DynamicRelationshipType.withName("knows")

	//2層目で探索を停止
	def s = {pos -> pos.depth() == 2} as StopEvaluator
	//name プロパティを持つNodeのみ返す。
	def r = {pos -> pos.currentNode().hasProperty("name")} as ReturnableEvaluator

	//Relationship=know で繋がっている Node を 2層目まで探索
	//（Relationship の向きはどちらでもよい）
	def t = n.traverse(Traverser.Order.BREADTH_FIRST, s, r, know, Direction.BOTH)

	//t.each を使うとエラーの発生頻度が高い
	//以下のコードでもエラーが発生する時と発生しない時がある
	t.getAllNodes().each {
		printName(it)
	}

	println "---------------"

} catch (e) {
//	e.printStackTrace()
} finally {
	tx.failure()
	tx.finish()
}

println("shutdown db ...")
db.shutdown()

