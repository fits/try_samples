import sbt._

class ScalatraMorphiaSampleProject(info: ProjectInfo) extends DefaultWebProject(info) {
	//以下の設定で war ファイルに scala-compiler.jar が入るようになる
	override def webappClasspath = super.webappClasspath +++ buildCompilerJar

	val jettyVersion = "8.0.0.M3"
	val scalatraVersion = "2.0.0-SNAPSHOT"
	val scalateVersion = "1.6.0-SNAPSHOT"
	val morphiaVersion = "1.00-SNAPSHOT"

	val jettyWebapp = "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "test"
	val servletapi = "javax.servlet" % "servlet-api" % "2.5" % "provided"

	val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion
	val scalatraScalate = "org.scalatra" %% "scalatra-scalate" % scalatraVersion
	val scalate = "org.fusesource.scalate" % "scalate-core" % scalateVersion

	val morphia = "com.google.code.morphia" % "morphia" % morphiaVersion

	val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
	val sonatypeNexusReleases = "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases"

	val fuseSourceSnapshots = "FuseSource Snapshot Repository" at "http://repo.fusesource.com/nexus/content/repositories/snapshots"

	val morphiaSnapshot = "Morphia Repo at Google Code" at "http://morphia.googlecode.com/svn/mavenrepo"

}
