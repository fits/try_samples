@Grapes([
    @Grab("org.apache.zookeeper:zookeeper:3.4.5"),
    @GrabExclude("com.sun.jmx#jmxri"),
    @GrabExclude("com.sun.jdmk#jmxtools"),
    @GrabExclude("javax.jms#jms")
])
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
