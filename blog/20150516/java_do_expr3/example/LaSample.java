
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.Optional;

public class LaSample {
	public static void main(String... args) {
		Optional<Integer> o1 = Optional.of(2);
		Optional<Integer> o2 = Optional.of(3);

		Opt<Integer> opt = new Opt<>();

		Optional<Integer> res = opt$do -> {
			let a = o1;
			let b = o2;
//			let b = Optional.empty();
			let c = Optional.of(4);
			return a + b + c * 2;
		};

		// Optional[13]
		System.out.println(res);

		Opt<String> opt2 = new Opt<>();

		Optional<String> res2 = opt2$do -> {
			let a = Optional.of("a");
			let b = Optional.of("b");
			let c = opt2$do -> {
				let c1 = Optional.of("c1");
				let c2 = Optional.of("c2");
				return c1 + "-" + c2;
			};
			return a + b + "/" + c;
		};
		// Optional[ab/c1-c2]
		System.out.println(res2);

		// Optional[***ba]
		System.out.println(opt2$do -> {
			let a = Optional.of("a");
			let b = Optional.of("b");
			return "***" + b + a;
		});
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
