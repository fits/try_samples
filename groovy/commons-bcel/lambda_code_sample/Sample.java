
import java.util.function.Predicate;

public class Sample {
	public static void main(String... args) {
		Predicate<Data> func = (d) -> d.getName() == "sample" && d.getValue() > 5;

		Data d = new Data("sample", 10);

		System.out.println(func.test(d));

		System.out.println(func.getClass());
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