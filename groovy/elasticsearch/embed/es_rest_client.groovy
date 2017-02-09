@Grab('org.elasticsearch.client:rest:5.2.0')
import org.elasticsearch.client.RestClient
import org.apache.http.HttpHost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity

def index = args[0]
def type = args[1]

def builder = RestClient.builder(new HttpHost('localhost', 9200))

def printContent = { res ->
	println res.entity.content.getText('UTF-8')
}

def json = new groovy.json.JsonBuilder()

json {
	time System.currentTimeMillis()
}

builder.build().withCloseable { client ->

	def empty = Collections.emptyMap()

	def content = new StringEntity(json.toString(), ContentType.APPLICATION_JSON)

	def r1 = client.performRequest('POST', "/${index}/${type}", empty, content)

	printContent r1

	sleep(1000)

	println '-----'

	def r2 = client.performRequest('GET', "/${index}/${type}/_search")

	printContent r2
}
