import sbt._
import de.element34.sbteclipsify._

class ListSampleProject(info: ProjectInfo) extends AndroidProject(info) with TypedResources with Eclipsify {

	override def androidPlatformName = "android-8"

}
