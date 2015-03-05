
import java.util.*;
import java.util.stream.*;

public class ListFlatMapSample {
	public static void main(String... args) {

		List<List<Integer>> data = Arrays.asList( 
			Arrays.asList(1, 2), 
			Arrays.asList(3, 4, 5), 
			Arrays.asList(6)
		);

		data.stream().flatMap(List::stream).forEach(System.out::println);
	}

}
