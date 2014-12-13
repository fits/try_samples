
import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Stream;

class ReduceSample {
	public static void main(String... args) {
		Function<String, BigDecimal> d = value -> new BigDecimal(value);

		BigDecimal res1 = data().reduce(
			BigDecimal.ZERO, 
			(acc, v) -> acc.add(d.apply(v)), 
			(acc, v) -> null
		);
		// 66
		System.out.println(res1);

		BigDecimal res2 = data().parallel().reduce(
			BigDecimal.ZERO, 
			(acc, v) -> acc.add(d.apply(v)), 
			(acc, v) -> null
		);
		// null
		System.out.println(res2);

		BigDecimal res3 = data().parallel().reduce(
			BigDecimal.ZERO, 
			(acc, v) -> acc.add(d.apply(v)), 
			(acc, v) -> acc.add(v)
		);
		// 66
		System.out.println(res3);
	}

	private static Stream<String> data() {
		return Stream.of("3", "10", "1", "2", "20", "30");
	}
}
