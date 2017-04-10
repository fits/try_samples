@Grab('org.apache.kafka:kafka_2.12:0.10.2.0')
@Grapes([
	@Grab('org.apache.curator:curator-test:3.3.0'),
	@GrabExclude('io.netty#netty:3.7.0.Final')
])
import kafka.server.KafkaServerStartable
import org.apache.curator.test.TestingServer
import org.apache.curator.test.InstanceSpec

def zkPort = 2181
def zkDir = 'zk-tmp'
def kafkaDir = 'kafka-logs'

def spec = new InstanceSpec(new File(zkDir), zkPort, -1, -1, false, -1)

def props = new Properties()
props.setProperty('zookeeper.connect', "localhost:${zkPort}")
props.setProperty('log.dir', kafkaDir)

new TestingServer(spec, false).withCloseable { zk ->
	zk.start()

	def kafka = KafkaServerStartable.fromProps(props)

	kafka.startup()

	println 'startup ...'

	System.in.read()

	kafka.shutdown()

	zk.stop()
}
