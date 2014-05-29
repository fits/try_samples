
import static java.util.stream.Collectors.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

class GiniImpurity {
	public static void main(String... args) {
		List<String> list = Arrays.asList("A", "B", "B", "C", "B", "A");

		System.out.println(calcGini(list));
	}

	private static double calcGini(List<String> list) {
		Map<String, Long> counts = countBy(list);

		Map<String, Double> prob = counts.entrySet().stream().collect(toMap(
			x -> x.getKey(),
			x -> x.getValue().doubleValue() / list.size()
		));

		return 1.0 - prob.values().stream().reduce(0.0, (acc, x) -> acc + Math.pow(x, 2));
	}

	private static Map<String, Long> countBy(List<String> list) {
		return list.stream().collect(groupingBy(s -> s, counting()));

		// 以下でも可
	//	return list.stream().collect(groupingBy(Function.identity(), counting()));
	}
}
