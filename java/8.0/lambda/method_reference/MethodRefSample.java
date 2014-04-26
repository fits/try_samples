
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class MethodRefSample {
	public static void main(String... args) {
		Consumer<String> println = System.out::println;

		String msg = "a,b,c,d";

		BiFunction<String, String, String> h1 = msg::replaceFirst;

		println.accept(h1.apply(",", "-"));
	}
}