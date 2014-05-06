@GrabResolver(name = 'maven-snapshot', root = 'http://repository.apache.org/content/groups/snapshots/')
@Grab('org.apache.bcel:bcel:6.0-SNAPSHOT')
import org.apache.bcel.Repository
import org.apache.bcel.classfile.Utility

def cls = Repository.lookupClass(args[0])

cls.methods.each {
	println "----- ${it.name} -----"
	println Utility.codeToString(it.code.code, it.constantPool, 0, -1)
}
