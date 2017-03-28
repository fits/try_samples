@Grab('org.apache.kafka:kafka_2.12:0.10.2.0')
import kafka.admin.ConsumerGroupCommand

def params = ['--bootstrap-server', 'localhost:9092', '--list'] as String[]

ConsumerGroupCommand.main(params)
