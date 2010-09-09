import sbt._

class SampleAndroidProject(info: ProjectInfo) extends AndroidProject(info) with MarketPublish {

	override def androidPlatformName = "android-8"
	val keyalias  = "wadays"
	override def keystorePath = Path.userHome / ".android" / "debug.keystore"

}
