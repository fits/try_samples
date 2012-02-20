
name := "groovy_sample_plugin_test"

organization := "fits"

seq(fits.sample.GroovySamplePlugin.groovySettings: _*)

//コンパイル時に Groovy ソースをコンパイルするための設定
Keys.compile <<= (Keys.compile in Compile) dependsOn fits.sample.GroovySamplePlugin.compile

//JAR ファイルにコンパイルした Groovy クラスを追加するための設定
unmanagedResourceDirectories in Compile <+= fits.sample.GroovySamplePlugin.outputDirectory
