import sodium.*;

class SnapshotSample {
	public static void main(String... args) {
		snapshotSample();
	}

	private static void snapshotSample() {
		System.out.println("*** Event.snapshot sample ***");

		EventSink<String> es = new EventSink<>();
		Listener esl = es.listen( msg -> System.out.println("event sink: " + msg) );

		BehaviorSink<Integer> bs = new BehaviorSink<>(1);
		Listener bsl = bs.value().listen( msg -> System.out.println("behavior: " + msg) );

		Event<Integer> se = es.snapshot(bs);
		Listener sel = se.listen( i -> System.out.println("snapshot event: " + i) );

		es.send("ES1");

		System.out.println("bh current value: " + bs.sample());

		System.out.println("---");

		bs.send(2);

		es.send("ES2");

		System.out.println("bh current value: " + bs.sample());

		sel.unlisten();
		esl.unlisten();
		bsl.unlisten();
	}
}