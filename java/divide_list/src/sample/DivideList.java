package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DivideList {
	public static void main(String... args) {
		System.out.println(divideList(range(0, 8), 3));
		System.out.println(divideList(range(0, 7), 3));
		System.out.println(divideList(range(0, 6), 3));

		System.out.println(divideList(range(0, 6), 6));
	}

	private static <T> List<List<T>> divideList(List<T> xs, int n) {
		int q = xs.size() / n;
		int m = xs.size() % n;

		return IntStream.range(0, n).collect(
				ArrayList::new,
				(acc, i) -> {
					int s = acc.stream().mapToInt(List::size).sum();
					int e = s + q + ((i < m)? 1: 0);
					acc.add(xs.subList(s, e));
				},
				ArrayList::addAll
		);
	}

	private static List<Integer> range(int start, int end) {
		return IntStream.range(start, end).boxed().collect(Collectors.toList());
	}
}