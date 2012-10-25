
import java.util.*;

public class FoldSample {

	public static void main(String[] args) {
		List<Integer> list = Arrays.asList(1, 3, 5, 7);

		int res = list.stream().fold(() -> 5, (a, b) -> a + b, null);
		// 21
		System.out.println(res);

		int res2 = list.stream().reduce(5, (a, b) -> a + b);
		// 21
		System.out.println(res2);

		Optional<Integer> res3 = list.stream().reduce((a, b) -> a + b);
		// Optional[16]
		System.out.println(res3);
	}
}
