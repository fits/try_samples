package sample.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.function.Function;

public class StringTypeSerializer<T> extends StdSerializer<T> {
    private final Function<T, String> getter;

    protected StringTypeSerializer(Class<T> cls, Function<T, String> getter) {
        super(cls);
        this.getter = getter;
    }

    @Override
    public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(getter.apply(t));
    }
}
