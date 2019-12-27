@Grab('org.apache.kafka:kafka_2.13:2.4.0')
@Grab('org.apache.zookeeper:zookeeper:3.5.6')
import kafka.server.KafkaServerStartable
import org.apache.zookeeper.server.ZooKeeperServerMain

def zkPort = args[0]

def zkArgs = [zkPort, 'tmp'] as String[]

def zk = new ZooKeeperServerMain()

Thread.start {
	zk.initializeAndRun(zkArgs)
}

while (zk.containerManager == null) {
	Thread.sleep(1000)
}

def kfProps = new Properties()
kfProps.setProperty('zookeeper.connect', "localhost:${zkPort}")
kfProps.setProperty('log.dir', './kafka-logs')

def kf = KafkaServerStartable.fromProps(kfProps)

kf.startup()

println 'startup ...'

System.in.read()

println 'close ...'

kf.shutdown()
zk.shutdown()
