
import com.mongodb.*

def con = new Mongo("localhost")

println "---- db names ------"

con.databaseNames.each {
	println it
}

def db = con.getDB("local")

println "---- collection names ------"

db.collectionNames.each {
	println it
}

println "--------------"

//コレクションの取得（無かったら新規作成）
def col = db.getCollection("test")

doc = new BasicDBObject()
doc.put("name", "testdb")
doc.put("title", "テストデータ")

//ドキュメントの登録
col.insert(doc)

println col.findOne()
