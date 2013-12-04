package sample;

import org.osgi.framework.BundleContext;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.osgi.ActorSystemActivator;

public class SampleActivator extends ActorSystemActivator {
	@Override
	public void configure(BundleContext context, ActorSystem system) {
		System.out.println("*** SampleActor.configure");

		registerService(context, system);

		ActorRef ref = system.actorOf(Props.create(SampleActor.class));
		ref.tell("aaaaaa", null);
	}
}