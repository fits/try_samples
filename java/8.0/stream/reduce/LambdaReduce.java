
import java.util.function.*;
import java.util.stream.*;

public class LambdaReduce {
	public static void main(String... args) {
		reduce1();
		reduce2();
		reduce3();
	}

	private static void reduce1() {
		Stream<Function<Integer, Integer>> st = Stream.of(
			a -> a + 2,
			a -> a * 3,
			a -> a + 5
		);

		Integer res = st.reduce(1, (acc, f) -> f.apply(acc), (acc, f) -> acc);

		// compile error (jdk 1.8.0_25 NullPointerException)
		//int res = st.reduce(1, (acc, f) -> f.apply(acc), (acc, f) -> acc);

		System.out.println(res);
	}

	private static void reduce2() {
		Stream<Function<Integer, Integer>> st = Stream.of(
			a -> a + 2,
			a -> a * 3,
			a -> a + 5
		);

		Function<Integer, Integer> stComb = st.reduce(a -> a, (acc, f) -> acc.andThen(f));

		int res = stComb.apply(1);

		System.out.println(res);
	}

	private static void reduce3() {
		Stream<Function<Integer, Integer>> st = Stream.of(
			a -> a + 2,
			a -> a * 3,
			a -> a + 5
		);

		Function<Integer, Integer> stComb = st.reduce(Function.identity(), (acc, f) -> acc.andThen(f));

		int res = stComb.apply(1);

		System.out.println(res);
	}

}
