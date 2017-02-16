package sample.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.function.Function;

public class StringTypeDeserializer<T> extends StdDeserializer<T> {
    private final Function<String, T> factory;

    protected StringTypeDeserializer(Class<T> cls, Function<String, T> factory) {
        super(cls);
        this.factory = factory;
    }

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return factory.apply(jsonParser.getValueAsString());
    }
}
