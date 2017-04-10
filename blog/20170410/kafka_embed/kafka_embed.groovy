@Grab('org.apache.kafka:kafka_2.12:0.10.2.0')
@Grab('org.apache.zookeeper:zookeeper:3.5.2-alpha')
import kafka.server.KafkaServerStartable
import org.apache.zookeeper.server.ZooKeeperServerMain

def zkPort = '2181'
def zkDir = 'zk-tmp'
def kafkaDir = 'kafka-logs'

def zk = new ZooKeeperServerMain()

Thread.start {
	zk.initializeAndRun([zkPort, zkDir] as String[])
}

def kafkaProps = new Properties()
kafkaProps.setProperty('zookeeper.connect', "localhost:${zkPort}")
kafkaProps.setProperty('log.dir', kafkaDir)

def kafka = KafkaServerStartable.fromProps(kafkaProps)

kafka.startup()

println 'startup ...'

System.in.read()

kafka.shutdown()
zk.shutdown()
