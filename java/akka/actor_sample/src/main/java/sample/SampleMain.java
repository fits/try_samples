package sample;

import akka.actor.*;

public class SampleMain {
	public static void main(String... args) throws Exception {
		ActorSystem system = ActorSystem.create("system");
		ActorRef ref = system.actorOf(Props.create(SampleActor.class));

		ref.tell("test data", null);

		system.shutdown();
	}
}
