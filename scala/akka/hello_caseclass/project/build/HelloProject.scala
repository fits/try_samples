import sbt._

class HelloProject(info: ProjectInfo) extends DefaultProject(info) with AkkaProject {
	override def mainClass = Some("fits.sample.Sample")

	val akkaTypedActor = akkaModule("typed-actor")
}
