@Grab('com.mashape.unirest:unirest-java:1.4.9')
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.JsonNode

def projectId = args[0]
def trackerId = args[1]
def statusId = args[2]
def title = args[3]

addShutdownHook {
//	println 'shutdown...'
	Unirest.shutdown()
}

def json = new groovy.json.JsonBuilder()

def config = new Properties()
config.load(new File('config.properties').newInputStream())

def postJson = { target, body ->
	Unirest
		.post("${config.base_url}/${target}.json")
		.header('X-Redmine-API-Key', config.access_key)
		.header('Content-type', 'application/json')
		.body(body)
		.asJson()
}

json.issue {
	project_id projectId
	tracker_id trackerId
	status_id statusId
	subject title
}

def res = postJson('issues', json.toString())

println res.body
