@Grab('org.apache.httpcomponents:httpclient:4.5')
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder

def req = new HttpGet(args[0])

def client = HttpClientBuilder.create().build()

def res = client.execute(req)

res.allHeaders.each {
	println it
}

println '---------------'

println res.entity.content.text
