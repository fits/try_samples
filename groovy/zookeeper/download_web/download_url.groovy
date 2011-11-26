@Grapes([
	@Grab("org.apache.zookeeper:zookeeper:3.4.0"),
	@GrabExclude("com.sun.jmx#jmxri"),
	@GrabExclude("com.sun.jdmk#jmxtools"),
	@GrabExclude("javax.jms#jms")
])
import java.util.concurrent.CountDownLatch
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.KeeperException
import static org.apache.zookeeper.Watcher.Event.KeeperState.*
import static org.apache.zookeeper.ZooDefs.Ids.*
import static org.apache.zookeeper.CreateMode.*

def signal = new CountDownLatch(1)

def zk = new ZooKeeper("localhost", 5000, {event ->
	if (event.state == SyncConnected) {
		signal.countDown()
	}
} as Watcher)

//Ú‘±‘Ò‚¿
signal.await()

def counter = 10
def root = "/download"

while(counter > 0) {
	def list = zk.getChildren(root, false)

	if (!list.isEmpty()) {
		def path = "${root}/${list.get(0)}"

		def data = zk.getData(path, false, null)

		def url = new URL(new String(data, "UTF-8"))
		def f = "${args[0]}/${url.file.split('/').last()}"

		try {
			zk.delete(path, -1)

			url.withInputStream {input ->
				new File(f).bytes = input.bytes
			}
			println "downloaded : ${url} => ${f}"

		} catch (KeeperException.NoNodeException e) {
			println "--- after deleted"
		} catch (IOException e) {
			println "failed: ${url}, ${e}"
		}
	}
	else {
		counter--
		Thread.sleep(1000)
	}
}

zk.close()
