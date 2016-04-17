@Grab('com.mashape.unirest:unirest-java:1.4.9')
import com.mashape.unirest.http.Unirest

import groovy.json.JsonBuilder

addShutdownHook {
	Unirest.shutdown()
}

def index = args[0]
def type = args[1]

def json = new JsonBuilder()

json {
	mappings {
		"${type}" {
			properties {
				host (type: 'string')
				date (type: 'date', format: 'yyyy-MM-dd HH:mm:ss')
				method (type: 'string')
				url (type: 'string')
				status (type: 'string')
				size (type: 'long')
				referer (type: 'string')
				useragent (type: 'string')
			}
		}
	}
}

def res = Unirest.put("http://localhost:9200/${index}")
	.body(json.toString())
	.asJson()

println res.body
