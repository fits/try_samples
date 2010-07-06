
import com.change_vision.jude.api.inf.project._
import com.change_vision.jude.api.inf.model._

case class Model(name: String)
case class UseCase(usecase: IUseCase) extends Model(usecase.getName())
case class Actor(actor: INamedElement) extends Model(actor.getName())

val proj = ProjectAccessorFactory.getProjectAccessor()
proj.open(args(0))

val list = proj.getProject.getOwnedElements.flatMap {
	case u: IUseCase => List(UseCase(u))
	case a if a.getStereotypes.contains("actor") => List(Actor(a))
	case _ => None
}

list.foreach {n =>
	println(n.getClass.getSimpleName + "\t" + n.name)
}

proj.close
