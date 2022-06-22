
import akka.actor.ActorSystem;
import akka.stream.javadsl.Source;

public class App {
    public static void main(String... args) {
        final var system = ActorSystem.create();

        var src = Source.range(1, 10);

        var done = src
                .map(i -> "num-" + i)
                .runForeach(System.out::println, system);

        done.thenRun(() -> system.terminate());
    }
}