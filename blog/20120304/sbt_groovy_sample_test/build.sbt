import Keys._

name := "sbt-groovy-sample-test"

organization := "fits"

//プラグインのキーを有効化
seq(fits.sample.GroovySamplePlugin.groovySettings: _*)

//コンパイル時に Groovy ソースをコンパイルするための設定
compile <<= (compile in Compile) dependsOn fits.sample.GroovySamplePlugin.groovyCompile

//コンパイルした Groovy クラスを JAR ファイルに追加するための設定
unmanagedResourceDirectories in Compile <+= fits.sample.GroovySamplePlugin.groovyOutputDirectory
