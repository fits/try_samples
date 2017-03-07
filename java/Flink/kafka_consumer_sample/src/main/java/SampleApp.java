
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import java.util.Properties;

public class SampleApp {
    public static void main(String... args) throws Exception {
        String topic = args[0];
        String group = args[1];

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", group);

        FlinkKafkaConsumer010<String> consumer =
                new FlinkKafkaConsumer010<>(topic, new SimpleStringSchema(), props);

        env.addSource(consumer).print();

        env.execute();
    }
}