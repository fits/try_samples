import sbt._

class ScalatraSampleProject(info: ProjectInfo) extends DefaultWebProject(info) {	//jetty-run を使用するために必要
	val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.25" % "test"
	//コンパイルに必要
	val servletapi = "javax.servlet" % "servlet-api" % "2.5"

	//以降は Scalatra の設定
	//Scalatra のダウンロードサイトの設定
	val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
	val sonatypeNexusReleases = "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases"
	//Scalatra のライブラリ設定
	val scalatra = "org.scalatra" %% "scalatra" % "2.0.0-SNAPSHOT"
}
