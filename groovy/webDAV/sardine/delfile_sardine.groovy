@GrabResolver(name = 'sardine-google-svn-repo', root = 'http://sardine.googlecode.com/svn/maven')
@Grab('com.googlecode.sardine:sardine:314')
@Grab('org.slf4j:slf4j-nop:1.6.6')
import com.googlecode.sardine.*

if (args.length < 3) {
	println "groovy delfile_sardine.groovy <user> <password> <file url>"
	return
}

def user = args[0]
def pass = args[1]

def sar = SardineFactory.begin(user, pass)

sar.delete args[2]
