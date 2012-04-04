@Grab('com.fasterxml.jackson.core:jackson-databind:2.0.0')
import com.fasterxml.jackson.databind.*

class SampleData {
	String name
	BigDecimal price
}

def mapper = new ObjectMapper()
def data = mapper.writeValueAsString([
	new SampleData(name: "test", price: 100),
	new SampleData(name: "test2", price: 0)
])

println data

println "----------------------"

def cls = mapper.typeFactory.constructCollectionType(List, SampleData)
def obj = mapper.readValue(data, cls)

obj.each {
	println it.dump()
}
