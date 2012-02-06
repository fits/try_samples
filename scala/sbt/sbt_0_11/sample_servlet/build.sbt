
name := "SampleServlet"

version := "1.0"

organization := "fits"

scalaVersion := "2.9.1"


seq(webSettings :_*)

libraryDependencies ++= Seq(
	"org.mortbay.jetty" % "jetty" % "6.1.22" % "container",
	"javax.servlet" % "servlet-api" % "2.5"
)