@Grab('com.github.lookfirst:sardine:5.0.1')
@Grab('org.slf4j:slf4j-nop:1.7.5')
import com.github.sardine.*

if (args.length < 4) {
	println "groovy filecopy_sardine.groovy <user> <password> <src url> <trg url>"
	return
}

def user = args[0]
def pass = args[1]
def srcUrl = args[2]
def trgUrl = args[3]

def sar = SardineFactory.begin(user, pass)

def trgBaseUrl = trgUrl.substring(0, trgUrl.lastIndexOf('/'))

if (!sar.exists(trgBaseUrl)) {
	sar.createDirectory(trgBaseUrl)
}

sar.copy(srcUrl, trgUrl)
