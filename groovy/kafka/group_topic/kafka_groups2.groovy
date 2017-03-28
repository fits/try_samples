@Grab('org.apache.kafka:kafka_2.12:0.10.2.0')
import kafka.admin.ConsumerGroupCommand.ConsumerGroupCommandOptions
import kafka.admin.ConsumerGroupCommand.KafkaConsumerGroupService

def params = ['--bootstrap-server', 'localhost:9092'] as String[]

def opts = new ConsumerGroupCommandOptions(params)

def svc = new KafkaConsumerGroupService(opts)

svc.listGroups().foreach {
	println it
}

svc.close()
