
import static java.util.stream.Collectors.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

class GiniImpurity {
	public static void main(String... args) {
		List<String> list = Arrays.asList("A", "B", "B", "C", "B", "A");

		System.out.println(calcGini1a(list));
		System.out.println(calcGini1b(list));
		System.out.println(calcGini1c(list));

		System.out.println(calcGini2(list));
	}

	private static double calcGini1a(List<String> list) {
		return 1 - countBy(list).values().stream().mapToDouble( x -> Math.pow(x.doubleValue() / list.size(), 2) ).sum();
	}

	private static double calcGini1b(List<String> list) {
		return 1 - countBy(list).values().stream().collect(summarizingDouble( x -> Math.pow(x.doubleValue() / list.size(), 2) )).getSum();
	}

	private static double calcGini1c(List<String> list) {
		Map<String, Long> counts = countBy(list);

		Map<String, Double> prob = counts.entrySet().stream().collect(toMap(
			s -> s.getKey(),
			s -> s.getValue().doubleValue() / list.size()
		));

		return 1.0 - prob.values().stream().reduce(0.0, (acc, v) -> acc + Math.pow(v, 2));
	}

	private static double calcGini2(List<String> list) {
		return combination(countBy(list)).stream().mapToDouble( s ->
			s.stream().mapToDouble( t -> 
				t.getValue().doubleValue() / list.size()
			).reduce(1.0, (a, b) -> a * b ) 
		).sum();

		/* 以下でも可。
		 * ただし、parallel Stream を使わない限り reduce の第三引数は使われない
		 */
		/*
		return combination(countBy(list)).stream().mapToDouble( s ->
			s.stream().reduce(
				1.0, 
				(acc, t) -> t.getValue().doubleValue() / list.size() * acc,
				(a, b) -> a + b
			)
		).sum();
		*/
	}

	private static <T> Map<T, Long> countBy(Collection<T> list) {
		return list.stream().collect(groupingBy( s -> s, counting()));

		// 以下でも可
	//	return list.stream().collect(groupingBy(Function.identity(), counting()));
	}

	private static <T, S> Collection<? extends List<Map.Entry<T, S>>> combination(Map<T, S> data) {
		ArrayList<List<Map.Entry<T, S>>> result = new ArrayList<>();

		for (Map.Entry<T, S> x : data.entrySet()) {
			for (Map.Entry<T, S> y : data.entrySet()) {
				if (!x.getKey().equals(y.getKey())) {
					result.add(Arrays.asList(x, y));
				}
			}
		}

		return result;
	}
}
