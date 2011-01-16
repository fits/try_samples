import sbt._

class SpringDataDocumentSample(info: ProjectInfo) extends DefaultProject(info) {
	lazy val springMavenSnapshot = "Springframework Maven SNAPSHOT Repository" at "http://maven.springframework.org/snapshot"

	lazy val springData = "org.springframework.data" % "spring-data-mongodb" % "1.0.0.BUILD-SNAPSHOT"

	override def mainClass = Some("fits.sample.Sample")
}
