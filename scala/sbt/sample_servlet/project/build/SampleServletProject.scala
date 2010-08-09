import sbt._

class SampleServletProject(info: ProjectInfo) extends DefaultWebProject(info) {
	val jetty6 = "org.mortbay.jetty" % "jetty" % "6.1.25" % "test"
	val servletapi = "javax.servlet" % "servlet-api" % "2.5"
}
