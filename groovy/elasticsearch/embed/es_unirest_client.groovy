@Grab('com.mashape.unirest:unirest-java:1.4.9')
import com.mashape.unirest.http.Unirest

def baseUrl = 'http://localhost:9200'

def index = args[0]
def type = args[1]

addShutdownHook {
	Unirest.shutdown()
}

def json = new groovy.json.JsonBuilder()

json {
	time System.currentTimeMillis()
}

def r1 = Unirest.post("${baseUrl}/${index}/${type}")
	.body(json.toString())
	.asJson()

println r1.body

sleep(1000)

println '-----'

def r2 = Unirest.get("${baseUrl}/${index}/${type}/_search").asJson()

println r2.body
