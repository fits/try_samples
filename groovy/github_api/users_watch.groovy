
import groovyx.gpars.actor.Actors
import groovy.json.JsonSlurper

def process(String url, Closure closure) {
	def con = new URL(url).openConnection()

	def data = con.inputStream.getText("UTF-8")

	try {
		new JsonSlurper().parseText(data).each {
			closure(it)
		}
	} catch (e) {
		System.err.println("error: ${url} : ${data}, ${e}")
	}

	def m = con.getHeaderField("Link") =~ /<([^>]*)>; rel="next"/

	if (m) {
		process(m[0][1], closure)
	}
}

System.in.readLines() collect {
	def items = it.split(",")

	def userWatch = Actors.actor {
		delegate.metaClass.onException = {
			println "failed: ${it}"
		}

		react {u ->
			def url = "https://api.github.com/users/${u.user}/watched?per_page=100"

			process(url) {json ->
				println "${u.id},${u.user},${json.id},${json.name}"
			}
		}
	}

	userWatch.send([id: items[0], user: items[1]])
	userWatch
} each {
	it.join()
}
