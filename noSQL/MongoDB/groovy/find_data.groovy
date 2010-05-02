
import com.mongodb.*

def con = new Mongo("localhost")

def db = con.getDB("local")

def col = db.getCollection("test-col")


//‘SŽæ“¾
col.find().each {
	println it
}

println "------- no=5 ---------"
//no=5 ‚ðŽæ“¾
col.find(new BasicDBObject("no", 5)).each {
	println it
}

println "------- value < 10 ---------"
//value < 10 ‚ðŽæ“¾
def query = new BasicDBObject("details.value", new BasicDBObject('$lt', 10))
col.find(query).each {
	println it
}
