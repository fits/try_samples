@Grab('com.mashape.unirest:unirest-java:1.4.9')
import com.mashape.unirest.http.Unirest

addShutdownHook {
	Unirest.shutdown()
}

def accessKey = args[0]

def getJson = { target ->
	Unirest
		.get("http://localhost:3000/${target}.json")
		.header('X-Redmine-API-Key', accessKey)
		.asJson()
}

def res = getJson 'issues'

res.body.object.issues.each {
	println "${it.id}, ${it.subject}"
}
