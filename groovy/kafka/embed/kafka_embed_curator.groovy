@Grab('org.apache.kafka:kafka_2.12:0.10.2.0')
@Grapes([
	@Grab('org.apache.curator:curator-test:3.3.0'),
	@GrabExclude('io.netty#netty:3.7.0.Final')
])
import kafka.server.KafkaServerStartable
import org.apache.curator.test.TestingServer
import org.apache.curator.test.InstanceSpec

def zkPort = args.length > 0 ? args[0] as int : 2181
def zkDir = args.length > 1 ? args[1] : 'tmp'

def spec = new InstanceSpec(new File(zkDir), zkPort, -1, -1, false, -1)

def props = new Properties()
props.setProperty('zookeeper.connect', "localhost:${zkPort}")
props.setProperty('log.dir', './kafka-logs')

new TestingServer(spec, false).withCloseable { zk ->
	zk.start()

	def kafka = KafkaServerStartable.fromProps(props)

	kafka.startup()

	println 'startup ...'

	System.in.read()

	kafka.shutdown()

	zk.stop()
}
