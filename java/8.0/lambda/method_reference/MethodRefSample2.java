
import java.util.function.Function;
import java.util.function.Supplier;

public class MethodRefSample2 {
	public static void main(String... args) {

		Data d1 = new Data("test1", 10);

		Function<Data, String> f = Data::getName;
		System.out.println(f.apply(d1));

		Data d2 = new Data("test2", 20);
		Supplier<String> f2 = d2::getName;
		// ˆÈ‰º‚Å‚à‰Â
		// Supplier<String> f2 = new Data("test2", 20)::getName;

		System.out.println(f2.get());
	}

	static class Data {
		private String name;
		private int value;

		public Data(String name, int value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return this.name;
		}

		public int getValue() {
			return this.value;
		}
	}
}