@Grab('com.fasterxml.jackson.core:jackson-databind:2.8.6')
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser

import groovy.transform.*

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

@ToString
@JsonSerialize(using = SampleIdSerializer)
@JsonDeserialize(using = SampleIdDeserializer)
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
