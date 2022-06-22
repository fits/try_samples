
import akka.actor.ActorSystem;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class App {
    public static void main(String... args) {
        final var system = ActorSystem.create();

        var source = Source.range(1, 10);
        var flow = Flow.<Integer, String>fromFunction(i -> "num-" + i);

        var graph = source
                .via(flow)
                .grouped(3)
                .map(g -> String.join(",", g))
                .to(Sink.foreach(System.out::println));

        graph.run(system);

        system.terminate();
    }
}