
import groovy.json.JsonSlurper

if (args.length < 1) {
	println "${new File(System.getProperty('script.name')).name} <user>"
	return
}

def user = args[0]
def url = "https://api.github.com/users/${user}/watched"

new URL(url).withInputStream {
	def res = new JsonSlurper().parseText(it.text)

	res.each {item ->
		println "${user},${item.name}"
	}
}
