
import groovyx.gpars.GParsExecutorsPool
import groovy.json.JsonSlurper

def process(String url, Closure closure) {
	def con = new URL(url).openConnection()

	def data = con.inputStream.getText("UTF-8")

	new JsonSlurper().parseText(data).each {
		closure(it)
	}

	def m = con.getHeaderField("Link") =~ /<([^>]*)>; rel="next"/

	if (m) {
		process(m[0][1], closure)
	}
}

GParsExecutorsPool.withPool(50) {
	System.in.readLines() eachParallel {
		def items = it.split(",")

		def userId = items[0]
		def user = items[1]

		def url = "https://api.github.com/users/${user}/watched?per_page=100"

		try {
			process(url) {json ->
				println "${userId},${user},${json.id},${json.name}"
			}
		} catch (e) {
			println "failed: ${it}"
		}
	}
}
