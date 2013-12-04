package sample;

import akka.actor.*;

public class SampleActor extends UntypedActor {
	@Override
	public void preStart() {
		System.out.println("preStart");
	}

	@Override
	public void onReceive(Object msg) {
		if (msg instanceof String) {
			System.out.println("*** " + msg);
		}
		else {
			unhandled(msg);
		}
	}
}
