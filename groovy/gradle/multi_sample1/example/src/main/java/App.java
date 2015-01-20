
import sample.Sample;

public class App {
	public static void main(String... args) {
		System.out.println( Sample.exec(Integer::parseInt, "100") );
	}
}