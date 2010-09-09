import sbt._

class SimpleAndroidAppProject(info: ProjectInfo) extends AndroidProject(info) {

	//Platform Android 2.2（API 8）を対象にするための設定
	override def androidPlatformName = "android-8"

}
