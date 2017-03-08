@Grab('org.apache.beam:beam-runners-direct-java:0.5.0')
@Grab('org.slf4j:slf4j-simple:1.7.24')
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.io.TextIO
import org.apache.beam.sdk.transforms.*
import org.apache.beam.sdk.values.*
import groovy.transform.CompileStatic

def srcFile = args[0]
def destFile = args[1]

@CompileStatic
class KvStr extends SimpleFunction<KV<String, Long>, String> {
	String apply(KV<String, Long> v) {
		println "*** ${v}"
		"${v.key} : ${v.value}"
	}
}

def opt = PipelineOptionsFactory.create()
def p = Pipeline.create(opt)

p.apply(TextIO.Read.from(srcFile))
	.apply(Count.perElement())
	.apply(MapElements.via(new KvStr() as SimpleFunction))
	.apply(TextIO.Write.to(destFile))

p.run().waitUntilFinish()
