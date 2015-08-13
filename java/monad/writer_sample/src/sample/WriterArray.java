package sample;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WriterArray<W, A> {
    private A value;
    private W[] logs;

    private WriterArray(A value) {
        this(value, null);
    }

    private WriterArray(A value, W[] logs) {
        this.value = value;
        this.logs = (logs == null)? (W[])new Object[0]: logs;
    }

    public static <W, A> WriterArray<W, A> unit(A value) {
        return new WriterArray<>(value);
    }

    public static <W, A> WriterArray<W, A> unit(A value, W[] logs) {
        return new WriterArray<>(value, logs);
    }

    public A value() {
        return value;
    }

    public W[] logs() {
        return logs;
    }

    public <B> WriterArray<W, B> bind(Function<A, WriterArray<W, B>> func) {
        WriterArray<W, B> res = func.apply(value);

        return unit(res.value,
                (W[])Stream.concat(Arrays.stream(logs), Arrays.stream(res.logs)).toArray());
    }

    @Override
    public String toString() {
        String logString = Arrays.stream(logs)
                .map(Object::toString)
                .collect(Collectors.joining(", ", "[", "]"));

        return "WriterArray(" + value + ", " + logString + ")";
    }
}
