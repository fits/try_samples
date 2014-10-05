
if (args.length < 2) {
	println '<ip> <netmask size>'
	return
}

def printHex = { println Long.toHexString(it) }

def maskSize = 32 - (args[1] as int)

def mask = -1 << maskSize

def ip = Integer.toUnsignedLong(InetAddress.getByName(args[0]).hashCode())

def from = ip & mask
def to = ip | ~mask

printHex ip
printHex mask

println '-----'

printHex from
println from

println ''

printHex to
println to
