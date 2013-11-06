// ZooKeeper 単体実行版
@Grapes([
    @Grab("org.apache.zookeeper:zookeeper:3.4.5"),
    @GrabExclude("com.sun.jmx#jmxri"),
    @GrabExclude("com.sun.jdmk#jmxtools"),
    @GrabExclude("javax.jms#jms")
])
import org.apache.zookeeper.server.ZooKeeperServerMain

ZooKeeperServerMain.main(args)
