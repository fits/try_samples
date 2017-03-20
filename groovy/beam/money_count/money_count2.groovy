@Grab('org.apache.beam:beam-runners-direct-java:0.6.0')
@Grab('org.slf4j:slf4j-simple:1.7.25')
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.io.TextIO
import org.apache.beam.sdk.transforms.*
import org.apache.beam.sdk.values.*
import groovy.transform.CompileStatic

def srcFile = args[0]
def destFile = args[1]

def opt = PipelineOptionsFactory.create()
def p = Pipeline.create(opt)

p.apply(TextIO.Read.from(srcFile))
	.apply(Count.perElement())
	.apply(ToString.kvs())
	.apply(TextIO.Write.to(destFile).withNumShards(1))

p.run().waitUntilFinish()
