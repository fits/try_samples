@Grab('org.apache.kafka:kafka_2.12:0.10.2.0')
import kafka.server.KafkaServerStartable

def props = new Properties()
props.setProperty('zookeeper.connect', 'localhost:2181')

def server = KafkaServerStartable.fromProps(props)

server.startup()

println 'startup ...'

System.in.read()

server.shutdown()
