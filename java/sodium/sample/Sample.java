import sodium.*;
import java.util.function.Consumer;

class Sample {
	public static void main(String... args) {
		Consumer<String> prtl = msg -> System.out.println(msg);

		BehaviorSink<Integer> a1 = new BehaviorSink<>(0);
		BehaviorSink<Integer> b1 = new BehaviorSink<>(0);

		Behavior<Integer> c1 = a1.lift( (a, b) -> a + b, b1 );

		Listener li = c1.value().listen( v -> prtl.accept("c1 = " + v) );

		prtl.accept("--- a1.send 10");
		a1.send(10);

		prtl.accept("--- b1.send 2");
		b1.send(2);

		prtl.accept("--- a1.send 30");
		a1.send(30);

		prtl.accept("--- b1.send 4");
		b1.send(4);

		li.unlisten();
	}
}