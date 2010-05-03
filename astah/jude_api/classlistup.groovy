import com.change_vision.jude.api.inf.project.*
import com.change_vision.jude.api.inf.model.*

scriptName = System.getProperty("script.name")

if (args.length < 1) {
	println "groovy ${scriptName} [jude fileName]"
	return
}

def pro = ProjectAccessorFactory.projectAccessor
pro.open(args[0])

def listUpClasses(packageObj) {
	packageObj.ownedElements.each {
		if (it instanceof IClass) {
			println it.name
		}
		else if(it instanceof IPackage) {
			listUpClasses(it)
		}
	}
}

listUpClasses(pro.project)

pro.close()
