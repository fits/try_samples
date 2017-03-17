
import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import org.apache.kafka.common.serialization.StringDeserializer;

public class SampleApp {
    public static void main(String... args) {
        String topic = args[0];
        String group = args[1];

        System.out.println("topic: " + topic + ", group: " + group);

        final ActorSystem system = ActorSystem.create();
        final Materializer materializer = ActorMaterializer.create(system);

        final ConsumerSettings<String, String> settings = ConsumerSettings.create(system,
                new StringDeserializer(), new StringDeserializer())
                .withBootstrapServers("localhost:9092")
                .withGroupId(group);

        Consumer.committableSource(settings, Subscriptions.topics(topic))
                .map(msg -> {
                    System.out.println(msg);
                    return msg;
                })
                .runWith(Sink.ignore(), materializer);
    }
}
