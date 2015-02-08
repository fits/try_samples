
import java.util.stream.IntStream;

class Sample {
	public String sample1() {
		return "sample";
	}

	public int sample2(int x) {
		return IntStream.range(0, x).map(n -> n * 2).sum();
	}

	class SampleChild {
		public void child1(int... nums) {
			IntStream.of(nums).filter(n -> {
				System.out.println(n);
				return n > 0 && n % 2 == 0;
			}).forEach(System.out::println);
		}

		private void child2() {
		}
	}
}
