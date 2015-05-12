
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.Optional;

public class DoExprSample {
	public static void main(String... args) {
		Optional<Integer> o1 = Optional.of(2);
		Optional<Integer> o2 = Optional.of(3);

		Opt<Integer> opt = new Opt<>();

		// replace by annotation processor
		Supplier<Optional<Integer>> res = opt$do -> {
			let a = o1;
			let b = o2;
//			let b = Optional.empty();
			let c = Optional.of(4);
			return a + b + c * 2;
		};

		// Optional[13]
		System.out.println(res.get());

		Opt<String> opt2 = new Opt<>();

		Supplier<Optional<String>> res2 = opt2$do -> {
			let a = Optional.of("a");
			let b = Optional.of("b");
			return a + b;
		};

		// Optional["ab"]
		System.out.println(res2.get());
	}

	static class Opt<T> {
		public Optional<T> bind(Optional<T> x, Function<T, Optional<T>> f) {
			return x.flatMap(f);
		}

		public Optional<T> unit(T v) {
			return Optional.ofNullable(v);
		}
	}
}
