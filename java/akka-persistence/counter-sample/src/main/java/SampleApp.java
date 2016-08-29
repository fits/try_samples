
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;

import lombok.val;

import sample.CounterAdd;
import sample.SampleActor;

import scala.concurrent.duration.FiniteDuration;
import scala.runtime.AbstractFunction1;
import scala.util.Try;

import java.util.concurrent.TimeUnit;

class SampleApp {
    public static void main(String... args) {
        val system = ActorSystem.apply("sample1");
        val actor = system.actorOf(Props.create(SampleActor.class, "data1"));

        actor.tell("dump", ActorRef.noSender());

        actor.tell(new CounterAdd(1), ActorRef.noSender());

        actor.tell("dump", ActorRef.noSender());

        actor.tell("snapshot", ActorRef.noSender());

        actor.tell(new CounterAdd(3), ActorRef.noSender());

        actor.tell("dump", ActorRef.noSender());

        val timeout = FiniteDuration.apply(5, TimeUnit.SECONDS);

        Patterns.gracefulStop(actor, timeout, "end").onComplete(
            new AbstractFunction1<Try<Boolean>, Void>() {
                @Override
                public Void apply(Try<Boolean> r) {
                    system.terminate();
                    return null;
                }
            }, 
            system.dispatcher()
        );
    }
}
