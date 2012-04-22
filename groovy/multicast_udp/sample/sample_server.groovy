
def buf = new byte[128]

// 224.0.0.1 ‚ğg‚¤‚Æ SocketException: error settings options ‚ª”­¶
def addr = InetAddress.getByName("224.0.0.2")

def ms = new MulticastSocket(41234)
def packet = new DatagramPacket(buf, buf.length)

ms.joinGroup(addr)

println "ready"

ms.receive(packet)

println "recv : ${new String(packet.data).trim()}"

ms.leaveGroup(addr)
ms.close()
