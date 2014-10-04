@Grab('com.maxmind.geoip2:geoip2:2.0.0')
import com.maxmind.geoip2.DatabaseReader

if (args.length < 2) {
	println '<maxmind db file> <ip>'
	return
}

def reader = new DatabaseReader.Builder(new File(args[0])).build()

def res = reader.city(InetAddress.getByName(args[1]))

println res.country
println res.city

reader.close()
