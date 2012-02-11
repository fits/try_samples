package fits.sample

import sbt._
import Keys._

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.tools.Compiler

object GroovySamplePlugin extends Plugin {

	val groovySource = SettingKey[File]("groovy-source")
	val outputDirectory = SettingKey[File]("groovy-output")
	val sources = TaskKey[Seq[File]]("groovy-sources")
	val compile = TaskKey[Unit]("groovy-compile", "Run Groovy compiler")

	val groovySettings = Seq(
		groovySource <<= sourceDirectory(_ / "main" / "groovy"),
		outputDirectory <<= crossTarget / "groovy",
		sources <<= groovySource map { dir => (dir * "*.groovy").get },
		compile <<= compileTask
	)

	def compileTask = (sources, outputDirectory) map {
		(src, destDir) => {

			val conf = new CompilerConfiguration()
			conf.setTargetDirectory(destDir)

			val compiler = new Compiler(conf)

			println("src : " + src)

			compiler.compile(src.toArray)
		}
	}
}
