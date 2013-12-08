@Grapes([
    @Grab("org.apache.zookeeper:zookeeper:3.4.5"),
    @GrabExclude("com.sun.jmx#jmxri"),
    @GrabExclude("com.sun.jdmk#jmxtools"),
    @GrabExclude("javax.jms#jms")
])
import org.apache.zookeeper.server.quorum.QuorumPeerMain

new QuorumPeerMain().initializeAndRun(args)

// スタンドアロン実行しかしないのであれば下記でも可
// org.apache.zookeeper.server.ZooKeeperServerMain.main(args)