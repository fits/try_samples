import sodium.*;

class EventSinkSample {
	public static void main(String... args) {

		EventSink<String> es = new EventSink<>();

		Listener li = es.listen(System.out::println);

		System.out.println("-----");

		es.send("A1");

		System.out.println("-----");

		es.send("B2");

		li.unlisten();
	}
}