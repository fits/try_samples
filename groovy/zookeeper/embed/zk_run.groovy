// ZooKeeper クラスタリング実行版
@Grab('org.apache.zookeeper:zookeeper:3.4.9')
@Grab('org.slf4j:slf4j-simple:1.7.21')
@Grab('log4j:log4j:1.2.17') // "java.lang.NoClassDefFoundError: org/apache/log4j/jmx/HierarchyDynamicMBean" の対策
import org.apache.zookeeper.server.quorum.QuorumPeerMain

if (args.length < 2) {
	println '[server number] [config file] ...'
	return
}

def serverNumber = args[0]

// myid ファイル作成・更新
new File(args[1]).eachLine {
	def line = it.trim()

	if (line.startsWith('dataDir=')) {
		def dataDir = line.split('=')[1]
		def myidFile = new File(dataDir, 'myid')

		myidFile.parentFile.mkdirs()
		myidFile.text = serverNumber
	}
}

new QuorumPeerMain().initializeAndRun(args.tail())
