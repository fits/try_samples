import java.util.function.Function;

public class ComposeSample {
	public static void main(String... args) {
		Function<Integer, Integer> plus = (x) -> x + 3;
		Function<Integer, Integer> times = (x) -> x * 2;

		// (4 + 3) * 2 = 14 : plus -> times
		System.out.println(plus.andThen(times).apply(4));
		// (4 * 2) + 3 = 11 : times -> plus
		System.out.println(plus.compose(times).apply(4));
	}
}