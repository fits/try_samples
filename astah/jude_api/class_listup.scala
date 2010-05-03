import com.change_vision.jude.api.inf.project.ProjectAccessorFactory
import com.change_vision.jude.api.inf.model.{IClass, IPackage}

if (args.length < 1) {
	println("scala classlistup.scala [jude fileName]")
	return
}

def listupClasses(packageObj: IPackage): Unit = {

	packageObj.getOwnedElements.foreach {el =>
		el match {
			case o: IClass => println(o.getName)
			case p: IPackage => listupClasses(p)
		}
	}
}

val pro = ProjectAccessorFactory.getProjectAccessor
pro.open(args(0))

listupClasses(pro.getProject)

pro.close
