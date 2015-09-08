@Grab('com.mashape.unirest:unirest-java:1.4.7')
import com.mashape.unirest.http.Unirest

// docker inspect <name>
def res = Unirest.get("http://127.0.0.1:2375/containers/${args[0]}/json").asJson()

println res.body
