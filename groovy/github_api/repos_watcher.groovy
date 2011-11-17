
import groovy.json.JsonSlurper

if (args.length < 2) {
	println "${new File(System.getProperty('script.name')).name} <user> <repos>"
	return
}

def printWatchers(String url) {
	def con = new URL(url).openConnection()

	new JsonSlurper().parseText(con.inputStream.text).each {
		println it.login
	}

	def m = con.getHeaderField("Link") =~ /<([^>]*)>; rel="next"/

	if (m) {
		printWatchers(m[0][1])
	}
}

def user = args[0]
def repos = args[1]

printWatchers("https://api.github.com/repos/${user}/${repos}/watchers?per_page=100")

