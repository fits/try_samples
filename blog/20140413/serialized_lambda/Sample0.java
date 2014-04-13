
import java.util.function.Predicate;

class Sample0 {
	public static void main(String... args) {
		int a = 3;

		Predicate<Integer> f = (x) -> x % a == 0;

		System.out.println(f.test(6));
	}
}
