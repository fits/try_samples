// not supported java 8 (lambda, method reference)
@Grab('org.codehaus.javancss:javancss:33.54')
import javancss.Javancss

def res = new Javancss(new File(args[0]))

if (res.lastError) {
	println res.lastError
	return
}

println "loc: ${res.getLOC()}, ml: ${res.ml}, ncss: ${res.ncss}, sl: ${res.sl}"

println "--- Object Metrics ---"

res.objectMetrics.each {
	println "name: ${it.name}, ccn: ${it.ccn}, ncss: ${it.ncss}"
}

println "--- Method Metrics ---"

res.functionMetrics.each {
	println "name: ${it.name}, ccn: ${it.ccn}, ncss: ${it.ncss}"
}
