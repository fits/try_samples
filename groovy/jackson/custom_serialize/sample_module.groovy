@Grab('com.fasterxml.jackson.core:jackson-databind:2.8.6')
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser

import groovy.transform.*

@ToString
class SampleId {
	String id
}

@ToString
class Sample {
	SampleId sampleId
	int value
}

def mapper = new ObjectMapper()

def d = new Sample(sampleId: new SampleId(id: "a1"), value: 10)

def json = mapper.writeValueAsString(d)

println json

println mapper.readValue(json, Sample.class)

println '--------------------'

class SampleIdSerializer extends StdSerializer<SampleId> {
	SampleIdSerializer() {
		super(SampleId)
	}

	void serialize(SampleId value, JsonGenerator gen, SerializerProvider provider) {
		gen.writeString(value.id)
	}
}

class SampleIdDeserializer extends StdDeserializer<SampleId> {
	SampleIdDeserializer() {
		super(SampleId)
	}

	SampleId deserialize(JsonParser p, DeserializationContext ctx) {
		new SampleId(id: p.getValueAsString())
	}
}

def mapper2 = new ObjectMapper()

def module = new SimpleModule()
module.addSerializer(new SampleIdSerializer())
module.addDeserializer(SampleId, new SampleIdDeserializer())

mapper2.registerModule(module)

def json2 = mapper2.writeValueAsString(d)

println json2

println mapper2.readValue(json2, Sample)
