import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class GroupingSample {
	public static void main(String... args) {
		List<Data> dataList = Arrays.asList(
			new Data("d1", "sample1"),
			new Data("d2", "sample2"),
			new Data("d3", "sample3")
		);

		List<Data> dataList2 = Arrays.asList(
			new Data("d1", "sample1"),
			new Data("d2", "sample2"),
			new Data("d3", "sample3"),
			new Data("d1", "sample1-b")
		);

		printMap("for", sample0(dataList));
		printMap("for", sample0(dataList2));

		printMap("groupingBy", sample0b(dataList));

		printMap("forEach", sample1(dataList));
		printMap("forEach", sample1(dataList2));

		printMap("toMap (2args)", sample2(dataList));
		try {
			printMap("toMap (2args)", sample2(dataList2));
		} catch(IllegalStateException e) {
			System.out.println("----- toMap (2args) -----");
			System.out.println("ERROR: " + e.getMessage());
		}

		printMap("toMap (2args)", sample2b(dataList));
		try {
			printMap("toMap (2args)", sample2b(dataList2));
		} catch(IllegalStateException e) {
			System.out.println("----- toMap (2args) -----");
			System.out.println("ERROR: " + e.getMessage());
		}

		printMap("toMap (3args) first", sample3(dataList));
		printMap("toMap (3args) first", sample3(dataList2));

		printMap("toMap (3args) last", sample3b(dataList2));

		printMap("groupingBy + collectingAndThen (toList)", sample4(dataList));
		printMap("groupingBy + collectingAndThen (toList)", sample4(dataList2));

		printMap("groupingBy + collectingAndThen(minBy)", sample4b(dataList2));

		printMap("collect1", sample5(dataList));
		printMap("collect1", sample5(dataList2));
	}

	private static Map<String, Data> sample0(List<Data> dataList) {
		Map<String, Data> res = new HashMap<>();
		for (Data d : dataList) {
			res.put(d.getId(), d);
		}

		return res;
	}

	private static Map<String, List<Data>> sample0b(List<Data> dataList) {
		Map<String, List<Data>> res = dataList.stream().collect(
				Collectors.groupingBy(Data::getId));

		return res;
	}

	// (1) forEach
	private static Map<String, Data> sample1(List<Data> dataList) {
		Map<String, Data> res = new HashMap<>();
		dataList.forEach(d -> res.put(d.getId(), d));

		return res;
	}

	// (2) toMap (2 args)
	private static Map<String, Data> sample2(List<Data> dataList) {
		Map<String, Data> res = dataList.stream().collect(
				Collectors.toMap(Data::getId, d -> d));

		return res;
	}
	// (2) toMap (2 args)
	private static Map<String, Data> sample2b(List<Data> dataList) {
		Map<String, Data> res = dataList.stream().collect(
				Collectors.toMap(Data::getId, UnaryOperator.identity()));

		return res;
	}

	// (2) toMap (3 args) first
	private static Map<String, Data> sample3(List<Data> dataList) {
		Map<String, Data> res = dataList.stream().collect(
				Collectors.toMap(Data::getId, d -> d, (d1, d2) -> d1));

		return res;
	}
	// (2) toMap (3 args) last
	private static Map<String, Data> sample3b(List<Data> dataList) {
		Map<String, Data> res = dataList.stream().collect(
				Collectors.toMap(Data::getId, d -> d, (d1, d2) -> d2));

		return res;
	}

	// (3) groupingBy + collectingAndThen
	private static Map<String, Data> sample4(List<Data> dataList) {
		Map<String, Data> res = dataList.stream().collect(
			Collectors.groupingBy(
				Data::getId,
				Collectors.collectingAndThen(
					Collectors.toList(),
					a -> a.get(0)
				)
			)
		);

		return res;
	}

	// (3) groupingBy + collectingAndThen
	private static Map<String, Data> sample4b(List<Data> dataList) {
		Map<String, Data> res = dataList.stream().collect(
			Collectors.groupingBy(
				Data::getId,
				Collectors.collectingAndThen(
					Collectors.minBy((a, b) -> 0),
					a -> a.get()
				)
			)
		);

		return res;
	}

	// (4) collect (3 args)
	private static Map<String, Data> sample5(List<Data> dataList) {
		Map<String, Data> res = dataList.stream().collect(
			HashMap::new,
			(m, d) -> m.put(d.getId(), d),
			Map::putAll
		);

		return res;
	}


	private static void printMap(String title, Map<String, ?> dataMap) {
		System.out.println("----- " + title + " -----");
		dataMap.entrySet().forEach(System.out::println);
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

		@Override
		public String toString() {
			return "Data(" + id + ", " + name + ")";
		}
	}
}
