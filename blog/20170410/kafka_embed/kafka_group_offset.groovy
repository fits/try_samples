@Grab('org.apache.kafka:kafka_2.12:0.10.2.0')
import kafka.admin.ConsumerGroupCommand.ConsumerGroupCommandOptions
import kafka.admin.ConsumerGroupCommand.KafkaConsumerGroupService

def group = args[0]

def params = ['--bootstrap-server', 'localhost:9092', '--group', group] as String[]

def opts = new ConsumerGroupCommandOptions(params)

def svc = new KafkaConsumerGroupService(opts)

def res = svc.describeGroup()

res._2.foreach {
	it.foreach { st ->
		println "topic = ${st.topic.value}, offset = ${st.offset.value}, partition = ${st.partition.value}"
	}
}

svc.close()
