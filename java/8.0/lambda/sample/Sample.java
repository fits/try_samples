
import java.util.*;

public class Sample {

	public static void main(String[] args) {
		List<String> list = Arrays.asList("a71", "b221", "a", "01");

		Collections.sort(list, (x, y) -> x.length() - y.length());

		for (String s : list) {
			System.out.println(s);
		}
	}
}
