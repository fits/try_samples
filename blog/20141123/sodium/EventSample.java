import sodium.*;

class EventSample {
	public static void main(String... args) {

		EventSink<String> es = new EventSink<>();

		Listener esl = es.listen(System.out::println);

		es.send("ES1");

		System.out.println("---");

		es.send("ES2");

		esl.unlisten();
	}
}