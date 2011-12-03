@Grapes([
	@Grab("org.apache.zookeeper:zookeeper:3.4.0"),
	@GrabExclude("com.sun.jmx#jmxri"),
	@GrabExclude("com.sun.jdmk#jmxtools"),
	@GrabExclude("javax.jms#jms")
])
import java.util.concurrent.CountDownLatch
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.Transaction
import org.apache.zookeeper.KeeperException
import static org.apache.zookeeper.Watcher.Event.KeeperState.*
import static org.apache.zookeeper.ZooDefs.Ids.*
import static org.apache.zookeeper.CreateMode.*

def countDownLoop = {count, closure ->
	def initialCount = count

	while (count > 0) {
		if (closure()) {
			count = initialCount
		}
		else {
			count--
			Thread.sleep(1000)
		}
	}
}

def downloadUrl = {URL url ->
	try {
		def f = "${args[0]}/${url.file.split('/').last()}"

		url.withInputStream {input ->
			new File(f).bytes = input.bytes
		}

		println "downloaded : ${url} => ${f}"

	} catch (IOException e) {
		println "failed: ${url}, ${e}"
	}
}


def signal = new CountDownLatch(1)

def zk = new ZooKeeper("localhost", 5000, {event ->
	if (event.state == SyncConnected) {
		signal.countDown()
	}
} as Watcher)

//ê⁄ë±ë“Çø
signal.await()

def root = "/download"

countDownLoop(10) {
	def result = false
	def list = zk.getChildren(root, false)

	if (list) {
		Collections.shuffle(list)

		def path = "${root}/${list.first()}"

		try {
			if (zk.getChildren(path, false).empty) {
				def lockPath = zk.create("${path}/lock-", null, OPEN_ACL_UNSAFE, EPHEMERAL_SEQUENTIAL)
				def lockList = zk.getChildren(path, false)

				if (lockPath == "${path}/${lockList.sort().first()}") {

					def data = zk.getData(path, false, null)

					downloadUrl(new URL(new String(data, "UTF-8")))

					def tr = zk.transaction()

					zk.getChildren(path, false).each {
						tr.delete("${path}/${it}", -1)
					}

					tr.delete(path, -1).commit()
				}
				else {
					zk.delete(lockPath, -1)
				}
			}
		} catch (KeeperException.NoNodeException e) {
			println "no node : ${path}"
		}
		result = true
	}
	result
}

zk.close()
