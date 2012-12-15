@Grab('com.gmongo:gmongo:1.0')
import com.gmongo.*

def mongo = new GMongo()
def db = mongo.getDB("access_log")

def logCount = { startDate, endDate, pattern ->
	db['apache'].find([
		time: [
			$gte: startDate,
			$lt:  endDate
		],
		url: [
			$regex: pattern
		]
	]).count()
}

def date = Date.parse('yyyy-MM-dd', args[0])

println logCount(date, date.plus(1), /.*\.(html|js|css).*/)
