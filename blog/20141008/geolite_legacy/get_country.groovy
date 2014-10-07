
if (args.length < 2) {
	println '<geolite country csv file> <ip address>'
	return
}

def toNumForIP = { Integer.toUnsignedLong(it.hashCode()) }
// 以下でも可
// def toNumForIP = { it.hashCode() & 0xffffffff }

def ip = toNumForIP( InetAddress.getByName(args[1]) )

new File(args[0]).eachLine() {
	def r = it.replaceAll('"', '').split(',')

	def from = r[2] as long
	def to = r[3] as long

	if (from <= ip && ip <= to) {
		println r.last()
		System.exit(0)
	}
}

println 'Unknown'
