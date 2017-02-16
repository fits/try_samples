package sample.counter.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Value;

import sample.json.StringTypeDeserializer;
import sample.json.StringTypeSerializer;

@Value(staticConstructor = "of")
@JsonSerialize(using = CounterId.Serializer.class)
@JsonDeserialize(using = CounterId.Deserializer.class)
public class CounterId {
	private String id;

	static class Serializer extends StringTypeSerializer<CounterId> {
		public Serializer() {
			super(CounterId.class, CounterId::getId);
		}
	}

	static class Deserializer extends StringTypeDeserializer<CounterId> {
		public Deserializer() {
			super(CounterId.class, CounterId::of);
		}
	}
}
