@Grab('com.mashape.unirest:unirest-java:1.4.9')
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.JsonNode

def issueId = args[0]
def note = args[1]

addShutdownHook {
//	println 'shutdown...'
	Unirest.shutdown()
}

def json = new groovy.json.JsonBuilder()

def config = new Properties()
config.load(new File('config.properties').newInputStream())

def putJson = { target, body ->
	Unirest
		.put("${config.base_url}/${target}.json")
		.header('X-Redmine-API-Key', config.access_key)
		.header('Content-type', 'application/json')
		.body(body)
		.asJson()
}

json.issue {
	notes note
}

def res = putJson("issues/${issueId}", json.toString())

println res.body
