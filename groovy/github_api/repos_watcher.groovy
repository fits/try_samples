
import groovy.json.JsonSlurper

if (args.length < 2) {
	println "${new File(System.getProperty('script.name')).name} <user> <repos>"
	return
}

def process(String url, Closure closure) {
	def con = new URL(url).openConnection()

	new JsonSlurper().parseText(con.inputStream.text).each {
		closure(it)
	}

	def m = con.getHeaderField("Link") =~ /<([^>]*)>; rel="next"/

	if (m) {
		process(m[0][1], closure)
	}
}

def user = args[0]
def repos = args[1]

process("https://api.github.com/repos/${user}/${repos}/watchers?per_page=100") {
	println "${it.id},${it.login},${it.url}"
}

