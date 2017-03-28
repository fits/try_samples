@Grab('org.apache.kafka:kafka_2.12:0.10.2.0')
import kafka.admin.ConsumerGroupCommand.ConsumerGroupCommandOptions
import kafka.admin.ConsumerGroupCommand.KafkaConsumerGroupService

def group = args[0]

def params = ['--bootstrap-server', 'localhost:9092', '--group', group] as String[]

def opts = new ConsumerGroupCommandOptions(params)

def svc = new KafkaConsumerGroupService(opts)

def res = svc.describeGroup()

//println res

res._2.foreach {
	it.foreach { st ->
		println "group = ${st.group.value}"
		println "topic = ${st.topic.value}"
		println "offset = ${st.offset.value}"
		println "lag = ${st.lag.value}"
		println "partition = ${st.partition.value}"
		println "logEndOffset = ${st.logEndOffset.value}"
	}
}

svc.close()
