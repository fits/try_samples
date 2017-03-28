@Grab('org.apache.zookeeper:zookeeper:3.5.2-alpha')
@Grab('org.slf4j:slf4j-nop:1.7.25')
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.Watcher

def path = args[0]

def zk = new ZooKeeper('localhost', 5000, {
	//println it
} as Watcher)

println '----- children -----'

zk.getChildren(path, false).each { println it }

println '----- data -----'

def d = zk.getData(path, false, null)

if (d != null) {
	println new String(d, 'UTF-8')
}

zk.close()
