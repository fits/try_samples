
import java.nio.*
import java.nio.channels.*
import java.nio.charset.Charset

def addr = InetAddress.getByName("224.0.0.2")

def dc = DatagramChannel.open(StandardProtocolFamily.INET)
	.bind(new InetSocketAddress(41234))

dc.join(addr, NetworkInterface.getByName("lo"))

def buf = ByteBuffer.allocateDirect(128)

println "ready"

while (true) {
	buf.clear()
	dc.receive(buf)

	def msg = Charset.defaultCharset().decode(buf.flip()).toString().trim()
	println "recv : ${msg}"

	if (msg == '') break
}

println "close"
dc.close()
