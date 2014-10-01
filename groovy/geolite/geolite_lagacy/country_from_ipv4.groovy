
if (args.length < 2) {
	println '<geolite csv file> <ip address>'
	return
}

def trim = { it.replaceAll('"', '') }
def toNumForIP = { Integer.toUnsignedLong(it.hashCode()) }

def csv = args[0]
def ip = toNumForIP( InetAddress.getByName(args[1]) )

new File(csv).eachLine() {
	def items = it.split(',')

	def from = trim(items[2]) as long
	def to = trim(items[3]) as long

	if (from <= ip && ip <= to) {
		println trim(items.last())
		System.exit(0)
	}
}

println 'Unknown'
