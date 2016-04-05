@Grab('com.mashape.unirest:unirest-java:1.4.9')
import com.mashape.unirest.http.Unirest

addShutdownHook {
	Unirest.shutdown()
}

// docker ps -a
def res = Unirest.get('http://127.0.0.1:2375/containers/json?all=1').asJson()

println res.body
