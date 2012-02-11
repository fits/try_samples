package fits.sample

import sbt._

object SamplePlugin extends Plugin {

	val sampleName = SettingKey[String]("sample-name")
	val sampleTask = TaskKey[Unit]("sample-task")

	val sampleSettings = Seq(
		sampleName := "sample",
		sampleTask <<= sampleName map { str => println("*** echo : " + str) }
	)

}
