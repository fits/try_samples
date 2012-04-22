
import java.nio.*
import java.nio.channels.*
import java.nio.charset.Charset

def addr = InetAddress.getByName("224.0.0.2")

def dc = DatagramChannel.open(StandardProtocolFamily.INET)
	.bind(new InetSocketAddress(41234))

dc.join(addr, NetworkInterface.getByName("lo"))

def buf = ByteBuffer.allocateDirect(100)

println "ready"

dc.receive(buf)

println "recv : ${Charset.defaultCharset().decode(buf.flip())}"

dc.close()
