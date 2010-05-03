import com.change_vision.jude.api.inf.project.*
import com.change_vision.jude.api.inf.model.*

scriptName = System.getProperty("script.name")

if (args.length < 1) {
	println "groovy ${scriptName} [jude fileName]"
	return
}

def pro = ProjectAccessorFactory.projectAccessor
pro.open(args[0])

def listUp(packageObj) {
	packageObj.ownedElements.each {
		if (it instanceof IUseCase) {
			println "${it.name}"
		}
		else if(it instanceof IPackage) {
			listUp(it)
		}
	}
}

listUp(pro.project)

pro.close()
