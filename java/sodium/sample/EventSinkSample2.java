import sodium.*;

class EventSinkSample2 {
	public static void main(String... args) {
		mapSample();

		System.out.println("");

		mergeSample();

		System.out.println("");

		snapshotSample();

		System.out.println("");

		holdSample();
	}

	private static void mapSample() {
		System.out.println("*** Event.map sample ***");

		EventSink<String> es = new EventSink<>();
		Listener esl = es.listen( msg -> System.out.println("event sink: " + msg) );

		Event<String> me = es.map( msg -> msg + "!!!" );
		Listener mel = me.listen( msg -> System.out.println("mapped event: " + msg) );

		es.send("ME1");
		es.send("ME2");

		mel.unlisten();
		esl.unlisten();
	}

	private static void mergeSample() {
		System.out.println("*** Event.merge sample ***");

		EventSink<String> es1 = new EventSink<>();
		Listener es1l = es1.listen( msg -> System.out.println("event sink1: " + msg) );

		EventSink<String> es2 = new EventSink<>();
		Listener es2l = es2.listen( msg -> System.out.println("event sink2: " + msg) );

		Event<String> me = es1.merge(es2);
		Listener mel = me.listen( msg -> System.out.println("merged event: " + msg) );

		es1.send("ES1-1");

		System.out.println("---");

		es2.send("ES2-1");

		System.out.println("---");

		es1.send("ES1-2");

		mel.unlisten();
		es2l.unlisten();
		es1l.unlisten();
	}

	private static void holdSample() {
		System.out.println("*** Event.hold sample ***");

		EventSink<String> es = new EventSink<>();
		Listener esl = es.listen( msg -> System.out.println("event sink: " + msg) );

		Behavior<String> bh = es.hold("BH1");
		Listener bhl = bh.value().listen( msg -> System.out.println("behavior: " + msg) );

		es.send("ES1");

		System.out.println("bh current value: " + bh.sample());

		System.out.println("---");

		es.send("ES2");

		System.out.println("bh current value: " + bh.sample());

		esl.unlisten();
		bhl.unlisten();
	}

	private static void snapshotSample() {
		System.out.println("*** Event.snapshot sample ***");

		EventSink<String> es = new EventSink<>();
		Listener esl = es.listen( msg -> System.out.println("event sink: " + msg) );

		Behavior<Integer> bh = new Behavior<>(1);
		Listener bhl = bh.value().listen( msg -> System.out.println("behavior: " + msg) );

		Event<Integer> se = es.snapshot(bh);
		Listener sel = se.listen( i -> System.out.println("snapshot event: " + i) );

		es.send("ES1");

		System.out.println("bh current value: " + bh.sample());

		System.out.println("---");

		es.send("ES2");

		System.out.println("bh current value: " + bh.sample());

		sel.unlisten();
		esl.unlisten();
		bhl.unlisten();
	}
}