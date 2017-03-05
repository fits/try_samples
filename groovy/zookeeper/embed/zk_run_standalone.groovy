// ZooKeeper 単体実行版
@Grab('org.apache.zookeeper:zookeeper:3.5.2-alpha')
@Grab('org.slf4j:slf4j-simple:1.7.24')
@Grab('org.mortbay.jetty:jetty:6.1.26')
import org.apache.zookeeper.server.ZooKeeperServerMain

ZooKeeperServerMain.main(args)