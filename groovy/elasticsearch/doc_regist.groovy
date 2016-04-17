@Grab('com.mashape.unirest:unirest-java:1.4.9')
import com.mashape.unirest.http.Unirest
import groovy.json.JsonBuilder

addShutdownHook {
	Unirest.shutdown()
}

def index = args[0]
def type = args[1]

def json = new JsonBuilder()

json (
	data: args[2]
)

def res = Unirest.post("http://localhost:9200/${index}/${type}")
	.body(json.toString())
	.asJson()

println res.body
