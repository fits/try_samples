package fits.sample

import sbt._
import Keys._

import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.tools.Compiler

object GroovySamplePlugin extends Plugin {

	val groovySourceDirectory = SettingKey[File]("groovy-source-directory")
	val groovyOutputDirectory = SettingKey[File]("groovy-output-directory")
	val groovySources = TaskKey[Seq[File]]("groovy-sources")
	val groovyCompile = TaskKey[Unit]("groovy-compile", "Run Groovy compiler")

	val groovySettings = Seq(
		groovySourceDirectory <<= sourceDirectory(_ / "main" / "groovy"),
		groovyOutputDirectory <<= crossTarget / "groovy",
		groovySources <<= groovySourceDirectory map { dir =>
			//Groovy スクリプトファイルのパスを取得
			//（PathFinder の get で Seq[File] 取得）
			(dir ** "*.groovy").get
		},
		groovyCompile <<= groovyCompileTask
	)

	def groovyCompileTask = (groovySources, groovyOutputDirectory, streams) map {
		(src, destDir, s) => {

			val conf = new CompilerConfiguration()
			conf.setTargetDirectory(destDir)

			val compiler = new Compiler(conf)

			s.log.info("src : " + src)

			compiler.compile(src.toArray)
		}
	}
}
