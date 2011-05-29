import sbt._

class EmbedJettySampleProject(info: ProjectInfo) extends DefaultProject(info) {

	val jetty = "org.eclipse.jetty" % "jetty-distribution" % "8.0.0.M3"
	val servletapi = "javax.servlet.jsp" % "jsp-api" % "2.1"

	override def mainClass = Some("fits.sample.Sample")
}
