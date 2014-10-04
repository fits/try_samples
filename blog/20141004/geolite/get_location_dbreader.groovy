@Grab('com.maxmind.db:maxmind-db:1.0.0')
import com.maxmind.db.Reader

if (args.length < 2) {
	println '<maxmind db file> <ip>'
	return
}

def reader = new Reader(new File(args[0]))

println reader.get(InetAddress.getByName(args[1]))

reader.close()
