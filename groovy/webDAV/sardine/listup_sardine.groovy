@Grab('com.github.lookfirst:sardine:5.0.1')
@Grab('org.slf4j:slf4j-nop:1.7.5')
import com.github.sardine.*

if (args.length < 3) {
	println "groovy listup_sardine.groovy <user> <password> <url>"
	return
}

def user = args[0]
def pass = args[1]
def url = args[2]

def sardine = SardineFactory.begin(user, pass)

sardine.list(url).each {
	println it
}
