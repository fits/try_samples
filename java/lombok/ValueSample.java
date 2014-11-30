
import lombok.val;
import lombok.Value;

class Sample {
	public static void main(String... args) {
		val d = new Data("sample1", 1);

		//d = new Data(null, 0);

		System.out.println(d);
	}

	@Value
	static class Data {
		private String name;
		private int value;
	}
}
