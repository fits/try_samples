
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import lombok.val;

import sample.CounterAdd;
import sample.SampleActor;

import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

class SampleApp {
    public static void main(String... args) {
        val system = ActorSystem.apply("sample1");
        val actor = system.actorOf(Props.create(SampleActor.class, "data1"));

        actor.tell("dump", ActorRef.noSender());

        actor.tell(new CounterAdd(3), ActorRef.noSender());

        actor.tell("dump", ActorRef.noSender());

        actor.tell("snapshot", ActorRef.noSender());

        actor.tell(new CounterAdd(5), ActorRef.noSender());

        actor.tell("dump", ActorRef.noSender());

        actor.tell("terminate", ActorRef.noSender());

        system.scheduler().scheduleOnce(
                FiniteDuration.apply(5, TimeUnit.SECONDS),
                system::terminate,
                system.dispatcher()
        );
    }
}
