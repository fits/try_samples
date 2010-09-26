import sbt._

class GaejSpecsSampleProject(info: ProjectInfo) extends AppengineProject(info) {
	//Specs setting for Scala 2.8.0
	val specs = "org.scala-tools.testing" % "specs_2.8.0" % "1.6.5" % "test"
}
