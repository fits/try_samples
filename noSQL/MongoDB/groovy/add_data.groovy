
import com.mongodb.*
import com.mongodb.util.JSONParser

def con = new Mongo("localhost")

def db = con.getDB("local")

//コレクションの取得（無かったら新規作成）
def col = db.getCollection("test-col")

(1..5).each {
	println it

	def doc = new BasicDBObject()
	doc.put("no", it)
	doc.put("title", "テストデータ" + it)

	//BasicDBObjectBuilder を使って BasicDBObject を生成
	details = BasicDBObjectBuilder.start(["value": it * it, "category": "A"]).get()
	doc.put("details", details)

	col.insert(doc)
}

//JSONParser を使って JSON 文字列から BasicDBObject を生成
def json = new JSONParser("{'no': 6, 'title': 'test6'}")
col.insert(json.parseObject())
