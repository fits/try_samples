
import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Stream;

class StreamConcatSample {
	public static void main(String... args) {
		Function<String, BigDecimal> d = value -> new BigDecimal(value);

		Stream<BigDecimal> s1 = Stream.of(d.apply("1"), d.apply("10"));
		Stream<BigDecimal> s2 = Stream.of(d.apply("2"), d.apply("3"), d.apply("4"));

		BigDecimal res = Stream.concat(s1, s2).reduce(BigDecimal.ZERO, BigDecimal::add);

		System.out.println(res);
	}

}
