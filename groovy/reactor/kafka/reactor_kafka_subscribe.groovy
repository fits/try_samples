@Grab('io.projectreactor.kafka:reactor-kafka:1.0.0.RELEASE')
@Grab('org.slf4j:slf4j-nop:1.7.25')
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import org.apache.kafka.common.TopicPartition

def topic = args[0]
def groupId = args[1]

def config = [
	'bootstrap.servers': 'localhost:9092',
	'key.deserializer': 'org.apache.kafka.common.serialization.StringDeserializer',
	'value.deserializer': 'org.apache.kafka.common.serialization.StringDeserializer',
	'group.id': groupId
]

def opt = ReceiverOptions.create(config)

def receiver = KafkaReceiver.create(opt.subscription([topic]))

receiver.receive().subscribe(
	{ res -> println res },
	{ err -> println err }
)

println 'start ...'

System.in.read()
