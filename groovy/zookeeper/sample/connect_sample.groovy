@Grapes([
	@Grab("org.apache.zookeeper:zookeeper:3.4.0"),
	@GrabExclude("com.sun.jmx#jmxri"),
	@GrabExclude("com.sun.jdmk#jmxtools"),
	@GrabExclude("javax.jms#jms")
])
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.Watcher
import java.util.concurrent.CountDownLatch

def signal = new CountDownLatch(1)

def zk = new ZooKeeper("localhost", 5000, {event ->
	println "process : ${event}"
	signal.countDown()
} as Watcher)

signal.await()

zk.close()


