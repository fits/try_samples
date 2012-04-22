
// 224.0.0.1 ‚ğg‚¤‚Æ SocketException: error settings options ‚ª”­¶
def addr = InetAddress.getByName("224.0.0.2")

def ms = new MulticastSocket(41234)

ms.joinGroup(addr)

println "ready"

while (true) {
	def buf = new byte[128]
	def packet = new DatagramPacket(buf, buf.length)

	ms.receive(packet)

	def msg = new String(packet.data).trim()
	println "recv : ${msg}"

	if (msg == '') break
}

println "close"

ms.leaveGroup(addr)
ms.close()
