@GrabResolver(name = 'maven-snapshot', root = 'http://repository.apache.org/content/groups/snapshots/')
@Grab('org.apache.bcel:bcel:6.0-SNAPSHOT')
import org.apache.bcel.Repository

def cls = Repository.lookupClass('java.lang.String')

cls.methods.each {
	println '*****'
	println it
	println '-----'
	println it.code
}
