import sodium.*;

class BehaviorSample {
	public static void main(String... args) {
		updatesSample();

		System.out.println("");

		valueSample();
	}

	private static void updatesSample() {
		System.out.println("*** Behavior.updates sample ***");

		BehaviorSink<String> bh = new BehaviorSink<>("BH1");
		Listener bhl = bh.updates().listen( msg -> System.out.println("behavior: " + msg) );

		bh.send("BH2");

		System.out.println("---");

		bh.send("BH3");

		bhl.unlisten();
	}

	private static void valueSample() {
		System.out.println("*** Behavior.value sample ***");

		BehaviorSink<String> bh = new BehaviorSink<>("BH1");
		Listener bhl = bh.value().listen( msg -> System.out.println("behavior: " + msg) );

		bh.send("BH2");

		System.out.println("---");

		bh.send("BH3");

		bhl.unlisten();
	}
}