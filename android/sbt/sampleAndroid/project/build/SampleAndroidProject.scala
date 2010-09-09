import sbt._

class SampleAndroidProject(info: ProjectInfo) extends AndroidProject(info) {

	override def androidPlatformName = "android-8"

}
