@Grab('com.fasterxml.jackson.core:jackson-databind:2.8.5')
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.databind.ObjectMapper

import groovy.transform.ToString

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes([
	@Type(SampleData),
	@Type(MessageData)
])
interface Data {}

@ToString
class SampleData implements Data {
	String id
	int value
}

@ToString
class MessageData implements Data {
	String id
	String message
}

def mapper = new ObjectMapper()

def d1 = new SampleData(id: 'd1', value: 10)
def json1 = mapper.writeValueAsString(d1)

println json1

println mapper.readValue(json1, SampleData)
println mapper.readValue(json1, Data) // base type

println '-----'

def d2 = new MessageData(id: 'd2', message: 'sample data')
def json2 = mapper.writeValueAsString(d2)

println json2

println mapper.readValue(json2, MessageData)
println mapper.readValue(json2, Data) // base type
