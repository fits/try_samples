import sbt._

class ScalatraMorphiaSampleProject(info: ProjectInfo) extends DefaultWebProject(info) {

	override def webappClasspath = super.webappClasspath +++ buildCompilerJar

	val jettyVersion = "8.0.0.M3"
	val scalatraVersion = "2.0.0-SNAPSHOT"
	val scalateVersion = "1.5.0-SNAPSHOT"
	val morphiaVersion = "1.00-SNAPSHOT"

	val jettyWebapp = "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "test"
	val servletapi = "javax" % "javaee-web-api" % "6.0" % "compile"

	val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion
	val scalatraScalate = "org.scalatra" %% "scalatra-scalate" % scalatraVersion
	val scalate = "org.fusesource.scalate" % "scalate-core" % scalateVersion

	val morphia = "com.google.code.morphia" % "morphia" % morphiaVersion


	val javaNet = "java.net" at "http://download.java.net/maven/2"

	val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
	val sonatypeNexusReleases = "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases"

	val fuseSourceSnapshots = "FuseSource Snapshot Repository" at "http://repo.fusesource.com/nexus/content/repositories/snapshots"

	val morphiaSnapshot = "Morphia Repo at Google Code" at "http://morphia.googlecode.com/svn/mavenrepo"

}
