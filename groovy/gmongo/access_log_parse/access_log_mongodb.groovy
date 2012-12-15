@Grab('com.gmongo:gmongo:1.0')
import com.gmongo.*

def mongo = new GMongo()
def db = mongo.getDB("access_log")

def df = new java.text.SimpleDateFormat('dd/MMM/yyyy:HH:mm:ss Z', Locale.US)

def convertDate = {dstr ->
	df.parse(dstr)
}

new File(args[1]).eachLine {
	def m = it =~ /^([^ ]*) ([^ ]*) ([^ ]*) \[([^]]*)\] "([^ ]*)(?: *([^ ]*) *([^ ]*))?" ([^ ]*) ([^ ]*) "(.*?)" "(.*?)"/

	if (m) {
		def p = m[0]

		db['apache'] << [
			server: args[0],
			host: p[1].trim(),
			time: convertDate(p[4]),
			method: p[5].trim(),
			url: p[6].trim(),
			protocol: p[7].trim(),
			status: p[8].trim(),
			size: p[9].trim(),
			referer: p[10].trim(),
			user_agent: p[11].trim()
		]
	}
}
