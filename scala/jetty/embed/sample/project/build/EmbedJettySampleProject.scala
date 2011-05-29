import sbt._

class EmbedJettySampleProject(info: ProjectInfo) extends DefaultProject(info) {

	val jettyVersion = "8.0.0.M3"

	val jettyWebapp = "org.eclipse.jetty" % "jetty-webapp" % jettyVersion
	val jettySecurity = "org.eclipse.jetty" % "jetty-security" % jettyVersion
	val jettyServlets = "org.eclipse.jetty" % "jetty-servlets" % jettyVersion
	val jettyDeploy = "org.eclipse.jetty" % "jetty-deploy" % jettyVersion

	override def mainClass = Some("fits.sample.Sample")
}
