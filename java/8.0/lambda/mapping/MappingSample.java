
import java.util.*;

public class MappingSample {

	public static void main(String[] args) {
		List<String> list = Arrays.asList("a71", "b221", "a", "01");

		list.stream().map(x -> x + "!!!").forEach(x -> System.out.println(x));
	}
}
