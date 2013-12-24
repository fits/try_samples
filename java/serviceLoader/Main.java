
import java.util.ServiceLoader;

public class Main {
	public static void main(String... args) {
		ServiceLoader<Processor> loader = ServiceLoader.load(Processor.class);

		for (Processor p: loader) {
			System.out.println("-----");
			System.out.println(p.getClass());

			p.process("123");
		}
	}
}