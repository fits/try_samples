
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.Optional;

public class LaSample {
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
