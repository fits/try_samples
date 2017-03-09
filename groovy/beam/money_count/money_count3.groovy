@Grab('org.apache.beam:beam-runners-direct-java:0.5.0')
@Grab('org.slf4j:slf4j-nop:1.7.24')
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.io.TextIO
import org.apache.beam.sdk.transforms.*
import org.apache.beam.sdk.values.*
import org.apache.beam.sdk.util.state.ValueState

import groovy.transform.CompileStatic

def srcFile = args[0]

@CompileStatic
class PrintFn<T> extends SimpleFunction<T, T> {
	T apply(T v) {
		println v
		v
	}
}

def opt = PipelineOptionsFactory.create()
def p = Pipeline.create(opt)

p.apply(TextIO.Read.from(srcFile))
	.apply(Count.perElement())
	.apply(ToString.kv())
	.apply(MapElements.via(new PrintFn<String>() as SimpleFunction))

	/* ERROR (IllegalArgumentException: Must take ProcessContext<> as ...)

	.apply(ParDo.of(new DoFn<String, String>() {
		@DoFn.ProcessElement
		void proc(DoFn<String, String>.ProcessContext c) {
			println c.element()
		}
	}))
	*/

p.run().waitUntilFinish()
