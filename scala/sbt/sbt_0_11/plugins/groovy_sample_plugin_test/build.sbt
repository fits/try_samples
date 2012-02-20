
name := "groovy_sample_plugin_test"

organization := "fits"

seq(fits.sample.GroovySamplePlugin.groovySettings: _*)

//コンパイル時に Groovy ソースをコンパイルするための設定
Keys.compile <<= (Keys.compile in Compile) dependsOn fits.sample.GroovySamplePlugin.compile
