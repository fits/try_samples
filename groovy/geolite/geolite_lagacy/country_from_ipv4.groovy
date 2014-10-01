
if (args.length < 2) {
	println '<geolite csv file> <ip address>'
	return
}

def toNumForIP = { Integer.toUnsignedLong(it.hashCode()) }

def ip = toNumForIP( InetAddress.getByName(args[1]) )

new File(args[0]).eachLine() {
	def items = it.replaceAll('"', '').split(',')

	def from = items[2] as long
	def to = items[3] as long

	if (from <= ip && ip <= to) {
		println items.last()
		System.exit(0)
	}
}

println 'Unknown'
