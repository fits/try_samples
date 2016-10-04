// ZooKeeper 単体実行版
@Grab('org.apache.zookeeper:zookeeper:3.4.9')
@Grab('org.slf4j:slf4j-simple:1.7.21')
@Grab('log4j:log4j:1.2.17') // "java.lang.NoClassDefFoundError: org/apache/log4j/jmx/HierarchyDynamicMBean" の対策
import org.apache.zookeeper.server.ZooKeeperServerMain

ZooKeeperServerMain.main(args)
