
import java.nio.*
import java.nio.channels.*
import java.nio.charset.Charset

def addr = InetAddress.getByName("224.0.0.2")

def dc = DatagramChannel.open()
	//ˆÈ‰º‚ÌÝ’è‚Í•K{‚Å‚Í‚È‚¢
	.setOption(StandardSocketOptions.IP_MULTICAST_TTL, 1)

def data = (args.length > 0)? args[0]: ""
def buf = ByteBuffer.wrap(data.bytes)

dc.send(buf, new InetSocketAddress(addr, 41234))

dc.close()
