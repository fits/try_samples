import java.util.function.*;

public class AndOrNegate {
	public static void main(String... args) {
		Predicate<Integer> p1 = (x) -> x > 0;
		Predicate<Integer> p2 = (x) -> x % 2 == 0;

		System.out.println(p1.test(1));
		System.out.println(p1.test(-1));

		System.out.println(p1.and(p2).test(2));
		System.out.println(p1.and(p2).negate().test(1));
		System.out.println(p1.or(p2).test(-2));
	}
}