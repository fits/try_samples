
import groovy.json.JsonSlurper

if (args.length < 1) {
	println "${new File(System.getProperty('script.name')).name} <user>"
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

process("https://api.github.com/users/${user}/watched") {
	println "${it.id},${it.name},${it.url}"
}
