@Grab('com.mashape.unirest:unirest-java:1.4.9')
import com.mashape.unirest.http.Unirest
import java.time.OffsetDateTime

def index = args[0]
def type = args[1]
def key = args[2]
def value = args[3]

addShutdownHook {
	Unirest.shutdown()
}

def json = new groovy.json.JsonBuilder()

json ([
	key: key,
	value: value,
	date: OffsetDateTime.now().toString()
])

//println json.toString()

def res = Unirest.post("http://localhost:9200/${index}/${type}")
					.body(json.toString())
					.asJson()

println res.body
