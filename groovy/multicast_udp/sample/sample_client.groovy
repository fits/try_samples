
def addr = InetAddress.getByName("224.0.0.2")

def ms = new MulticastSocket()
ms.timeToLive = 1

def data = (args.length > 0)? args[0]: ""
def buf = data.bytes

def packet = new DatagramPacket(buf, buf.length, addr, 41234)

ms.send(packet)

ms.close()
