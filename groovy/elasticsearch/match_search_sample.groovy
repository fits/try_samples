@Grab('com.mashape.unirest:unirest-java:1.3.26')
import com.mashape.unirest.http.Unirest
import groovy.json.JsonBuilder

def index = args[0]
def type = args[1]

def builder = new JsonBuilder()

builder {
	query {
		match {
			name {
				query args[2]
			}
		}
	}
}

def res = Unirest.post("http://localhost:9200/${index}/${type}/_search").body(builder.toString()).asJson()

println res.status
println res.statusText
println res.body
