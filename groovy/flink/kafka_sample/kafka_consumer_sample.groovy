@Grab('org.apache.flink:flink-streaming-java_2.11:1.2.0')
@Grab('org.apache.flink:flink-connector-kafka-0.10_2.11:1.2.0')
@Grab('org.scala-lang:scala-library:2.11.8')
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.streaming.util.serialization.SimpleStringSchema

def topic = args[0]
def group = args[1]

def props = [
	'bootstrap.servers': 'localhost:9092',
	'group.id': group
] as Properties

def consumer = new FlinkKafkaConsumer010(topic, new SimpleStringSchema(), props)
def env = StreamExecutionEnvironment.getExecutionEnvironment()

env.addSource(consumer).print()

env.execute()
