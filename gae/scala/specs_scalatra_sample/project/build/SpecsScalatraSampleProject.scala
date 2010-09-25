import sbt._

class SpecsScalatraSampleProject(info: ProjectInfo) extends AppengineProject(info) {

	//Specs setting for Scala 2.8.0
	val specs = "org.scala-tools.testing" % "specs_2.8.0" % "1.6.5" % "test"

	//以降は Scalatra の設定
	//Scalatra のダウンロードサイトの設定
	val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
	val sonatypeNexusReleases = "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases"
	//Scalatra のライブラリ設定
	val scalatra = "org.scalatra" %% "scalatra" % "2.0.0-SNAPSHOT"
}
