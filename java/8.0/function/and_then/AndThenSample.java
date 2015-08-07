
import java.util.function.*;

public class AndThenSample {
	public static void main(String... args) {

		Function<String, Integer> f = Function.<String>identity().andThen(String::trim).andThen(String::length);

		System.out.println(f.apply("  123  "));
	}
}