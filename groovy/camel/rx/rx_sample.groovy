@Grab('org.apache.camel:camel-core:2.19.2')
@Grab('org.apache.camel:camel-rx:2.19.2')
@Grab('org.slf4j:slf4j-nop:1.7.25')
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.rx.ReactiveCamel

class SampleRoute extends RouteBuilder {
	void configure() {
		from('direct:test').to('direct:out')
	}
}

def ctx = new DefaultCamelContext()

ctx.addRoutes(new SampleRoute())

def rx = new ReactiveCamel(ctx)

rx.toObservable('direct:out')
	.map { "${it.body} !!!" }
	.subscribe { println it }

def tpl = ctx.createProducerTemplate()

ctx.start()

(1..10).each {
	tpl.sendBody('direct:test', "data${it}")
}

ctx.stop()
