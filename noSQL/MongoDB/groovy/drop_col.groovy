
import com.mongodb.*
import com.mongodb.util.JSONParser

def con = new Mongo("localhost")

def db = con.getDB("local")

def col = db.getCollection("test-col")

col.drop()
