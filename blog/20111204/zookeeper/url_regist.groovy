@Grapes([
	@Grab("org.apache.zookeeper:zookeeper:3.4.0"),
	@GrabExclude("com.sun.jmx#jmxri"),
	@GrabExclude("com.sun.jdmk#jmxtools"),
	@GrabExclude("javax.jms#jms")
])
import java.util.concurrent.CountDownLatch
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.Watcher
import static org.apache.zookeeper.Watcher.Event.KeeperState.*
import static org.apache.zookeeper.ZooDefs.Ids.*
import static org.apache.zookeeper.CreateMode.*

def signal = new CountDownLatch(1)

def zk = new ZooKeeper("localhost", 5000, {event ->
	if (event.state == SyncConnected) {
		signal.countDown()
	}
} as Watcher)

//ê⁄ë±ë“Çø
signal.await()

def root = "/download"

if (zk.exists(root, false) == null) {
	zk.create(root, null, OPEN_ACL_UNSAFE, PERSISTENT)
}

System.in.readLines() each {
	def path = zk.create("${root}/url-", it.getBytes("UTF-8"), OPEN_ACL_UNSAFE, PERSISTENT_SEQUENTIAL)

	println "created path : ${path}"
}

zk.close()
