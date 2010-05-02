
import com.mongodb.*
import com.mongodb.util.JSONParser

def con = new Mongo("localhost")

def db = con.getDB("local")

def col = db.getCollection("test-col")

//no=1 のデータを削除
def query = new BasicDBObject("no", 3)
col.find(query).each {
	println it
	col.remove(it)
}

println "-------------"

//全取得
col.find().each {
	println it
}

