@Grab('org.apache.kafka:kafka_2.12:0.11.0.1')
@Grab('org.apache.zookeeper:zookeeper:3.5.3-beta')
import kafka.server.KafkaServerStartable
import org.apache.zookeeper.server.ZooKeeperServerMain

def zkPort = args[0]

def zkArgs = [zkPort, 'tmp'] as String[]

def zk = new ZooKeeperServerMain()

Thread.start {
	zk.initializeAndRun(zkArgs)
}

def kfProps = new Properties()
kfProps.setProperty('zookeeper.connect', "localhost:${zkPort}")
kfProps.setProperty('log.dir', './kafka-logs')

def kf = KafkaServerStartable.fromProps(kfProps)

kf.startup()

println 'startup ...'

System.in.read()

kf.shutdown()
zk.shutdown()
