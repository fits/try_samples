package sample;

import reactor.Environment;
import reactor.bus.Event;
import reactor.bus.EventBus;

import static reactor.bus.selector.Selectors.*;

public class EventBusSample {
	public static void main(String... args) {
		Environment env = new Environment();

		EventBus eb = EventBus.config().env(env).get();

		eb.on($("/sample/id"), ev -> System.out.println("$:, " + ev));

		eb.on(R("/sample/(.+)"), ev -> System.out.println("R:" + ev.getHeaders().get("group1") + ", " + ev));

		eb.on(U("/sample/{id}"), ev -> System.out.println("U:" + ev.getHeaders().get("id") + ", " + ev));

		eb.notify("/sample/id", Event.wrap("test1"));
		eb.notify("/sample/10", Event.wrap("test2"));
		eb.notify("/sample", Event.wrap("test3"));

		env.shutdown();
	}
}
