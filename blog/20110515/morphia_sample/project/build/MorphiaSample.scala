import sbt._

class MorphiaSample(info: ProjectInfo) extends DefaultProject(info) {

	lazy val morphiaSnapshot = "Morphia Repo at Google Code" at "http://morphia.googlecode.com/svn/mavenrepo"

	lazy val morphia = "com.google.code.morphia" % "morphia" % "1.00-SNAPSHOT"

	override def mainClass = Some("fits.sample.Sample")
}
