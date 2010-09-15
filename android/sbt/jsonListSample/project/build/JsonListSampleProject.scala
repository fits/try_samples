import sbt._

class JsonListSampleProject(info: ProjectInfo) extends AndroidProject(info) with TypedResources {

	override def androidPlatformName = "android-8"

}
