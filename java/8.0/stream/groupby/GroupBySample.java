
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class GroupBySample {
	public static void main(String... args) {
		List<Data> dataList = Arrays.asList(
			new Data("d1", "sample1"),
			new Data("d2", "test2"),
			new Data("d3", "aaa"),
			new Data("d1", "sample1b")
		);

		Map<String, List<Data>> res1 = dataList.stream().collect(
			Collectors.groupingBy(Data::getId)
		);

		res1.entrySet().forEach(System.out::println);

		System.out.println("-----");

		Map<String, Data> res2 = dataList.stream().collect(
			Collectors.toMap(Data::getId, UnaryOperator.identity(), (a, b) -> a)
		);

		res2.entrySet().forEach(System.out::println);

		System.out.println("-----");

		Map<String, Data> res2b = dataList.stream().collect(
			Collectors.toMap(Data::getId, a -> a, (a, b) -> a)
		);

		res2b.entrySet().forEach(System.out::println);

		System.out.println("-----");

		Map<String, Optional<Data>> res3 = dataList.stream().collect(
			Collectors.groupingBy(Data::getId, toHead())
		);

		res3.entrySet().forEach(System.out::println);

		System.out.println("-----");

		Optional<Data> res3e = Stream.<Data>empty().collect(toHead());

		System.out.println(res3e);

		System.out.println("-----");

		Map<String, Optional<Data>> res4 = dataList.stream().collect(
			Collectors.groupingBy(Data::getId, Collectors.minBy((a, b) -> 0))
		);

		res4.entrySet().forEach(System.out::println);

		System.out.println("-----");

		Map<String, Data> res5 = dataList.stream().collect(
			Collectors.groupingBy(
				Data::getId, 
				Collectors.collectingAndThen(
					Collectors.minBy((a, b) -> 0),
					a -> a.orElse(null)
				)
			)
		);

		res5.entrySet().forEach(System.out::println);

		System.out.println("-----");

		Map<String, Data> res6 = dataList.stream().collect(
			Collectors.groupingBy(
				Data::getId, 
				Collectors.collectingAndThen(
					Collectors.toList(),
					a -> a.get(0)
				)
			)
		);

		res6.entrySet().forEach(System.out::println);

		System.out.println("-----");

		Map<String, Data> res7 = dataList.stream().collect(
			() -> new HashMap<String, Data>(),
			(m, d) -> m.put(d.getId(), d),
			Map::putAll
		);

		res7.entrySet().forEach(System.out::println);
	}

	private static <T> Collector<T, ?, Optional<T>> toHead() {
		return Collector.<T, List<T>, Optional<T>>of(
			ArrayList::new, 
			List::add, 
			(a, b) -> {
				a.addAll(b);
				return a;
			}, 
			r -> r.isEmpty()? Optional.empty(): Optional.ofNullable(r.get(0))
		);
	}

	static class Data {
		private String id;
		private String name;

		public Data(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "Data(" + id + ", " + name + ")";
		}
	}
}

