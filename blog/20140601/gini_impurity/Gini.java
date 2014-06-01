
import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

class Gini {
	public static void main(String... args) {
		List<String> list = Arrays.asList("A", "B", "B", "C", "B", "A");

		System.out.println(countBy(list));
		System.out.println(combination(countBy(list)));

		System.out.println( giniA(list) );
		System.out.println( giniB(list) );
	}

	// (a) 1 - (AA + BB + CC)
	private static double giniA(List<String> xs) {
		return 1 - countBy(xs).values().stream().mapToDouble( x -> Math.pow(x.doubleValue() / xs.size(), 2) ).sum();
	}

	// (b) AB + AC + BA + BC + CA + CB
	private static double giniB(List<String> xs) {
		return combination(countBy(xs)).stream().mapToDouble( s ->
			s.stream().mapToDouble( t -> 
				t.getValue().doubleValue() / xs.size()
			).reduce(1.0, (a, b) -> a * b ) 
		).sum();
	}

	private static <T> Map<T, Long> countBy(Collection<T> xs) {
		return xs.stream().collect(groupingBy(Function.identity(), counting()));
	}

	private static <T, S> Collection<? extends List<Map.Entry<T, S>>> combination(Map<T, S> data) {

		return data.entrySet().stream().flatMap( x ->
			data.entrySet().stream().flatMap ( y ->
				(x.getKey().equals(y.getKey()))? Stream.empty(): Stream.of(Arrays.asList(x, y))
			)
		).collect(toList());
	}
}
