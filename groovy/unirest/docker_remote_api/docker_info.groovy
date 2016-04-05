@Grab('com.mashape.unirest:unirest-java:1.4.9')
import com.mashape.unirest.http.Unirest

addShutdownHook {
	Unirest.shutdown()
}

def res = Unirest.get('http://127.0.0.1:2375/info').asJson()

println res.body
