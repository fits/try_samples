@Grapes([
	@Grab('org.apache.curator:curator-test:3.3.0'),
	@GrabExclude('io.netty#netty:3.7.0.Final')
])
@Grab('org.slf4j:slf4j-nop:1.7.25')
import org.apache.curator.test.TestingServer
import org.apache.curator.test.InstanceSpec

def zkPort = args.length > 0 ? args[0] as int : 2181
def zkDir = args.length > 1 ? new File(args[1]) : null

def spec = new InstanceSpec(zkDir, zkPort, -1, -1, false, -1)

new TestingServer(spec, false).withCloseable { server ->
	server.start()

	println 'started ...'

	System.in.read()

	server.stop()
}
