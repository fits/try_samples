import sbt._

class HttpSampleProject(info: ProjectInfo) extends DefaultProject(info) {

	override def mainClass = Some("fits.sample.Sample")

	lazy val glassFishRepo = "Java.net Maven 2 Repository for GlassFish" at "http://download.java.net/maven/glassfish/"
	lazy val javaNetRepo = "Java.net Repository for Grizzly" at "http://download.java.net/maven/2"

	lazy val grizzly = "org.glassfish.grizzly" % "grizzly-http-server" % "2.0.1"

}
