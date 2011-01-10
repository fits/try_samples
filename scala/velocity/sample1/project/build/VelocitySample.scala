import sbt._

class VelocitySample(info: ProjectInfo) extends DefaultProject(info) {
	val velocity = "org.apache.velocity" % "velocity" % "1.7"
	override def mainClass = Some("fits.sample.Sample")
}
