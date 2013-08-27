import java.util.function.*;

public class AndThenCompose {
	public static void main(String... args) {
		Function<Integer, Integer> f1 = (x) -> x * 3;
		Function<Integer, Integer> f2 = (x) -> x + 2;

		System.out.println("andThen = " + f1.andThen(f2).apply(3));
		System.out.println("compose = " + f1.compose(f2).apply(3));
	}
}