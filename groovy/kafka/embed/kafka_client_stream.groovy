@Grab('org.apache.kafka:kafka-streams:0.10.2.0')
@Grab('org.slf4j:slf4j-simple:1.7.24')
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.kstream.KStreamBuilder
import org.apache.kafka.common.serialization.Serdes

def props = new Properties()

props.put('application.id', 'sample-1')
props.put('bootstrap.servers', 'localhost:9092')
props.put('key.serde', Serdes.String().class)
props.put('value.serde', Serdes.String().class)

def builder = new KStreamBuilder()
builder.stream('sample').print()

def streams = new KafkaStreams(builder, props)

streams.start()

System.in.read()

streams.close()