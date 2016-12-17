
import akka.actor.ActorSystem;

import eventstore.EventNumber;
import eventstore.j.EsConnectionFactory;

import static scala.compat.java8.JFunction.*;

import lombok.val;

public class SampleApp {
    public static void main(String... args) {
        val system = ActorSystem.create();
        val con = EsConnectionFactory.create(system);

        val eventNo = new EventNumber.Exact(0);

        val res = con.readStreamEventsForward("sample1", eventNo, 10, false, null);

        res.foreach(func(r -> {
            r.eventsJava().forEach(System.out::println);

            system.terminate();

            return null;
        }), system.dispatcher());

    }
}
