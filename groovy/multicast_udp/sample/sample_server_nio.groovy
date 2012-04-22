
import java.nio.*
import java.nio.channels.*
import java.nio.charset.Charset

def addr = InetAddress.getByName("224.0.0.2")

//loopback interface
def ni = NetworkInterface.getByName("lo")

def dc = DatagramChannel.open(StandardProtocolFamily.INET)
	.bind(new InetSocketAddress(41234))
	.setOption(StandardSocketOptions.IP_MULTICAST_IF, ni)

dc.join(addr, ni)

def buf = ByteBuffer.allocateDirect(100)

println "ready"

dc.receive(buf)

println "recv : ${Charset.defaultCharset().decode(buf.flip())}"

dc.close()
