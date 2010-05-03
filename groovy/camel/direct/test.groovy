
import org.apache.camel.Processor
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockComponent

class SampleRoute extends RouteBuilder {
	void configure() {
		println "--- call configure()"

		from("direct:start").process({println "process $it"} as Processor)
	}
}



ctx = new DefaultCamelContext()
ctx.addRoutes(new SampleRoute())

template = ctx.createProducerTemplate()

println "start"

ctx.start()

println "end start"

(1..3).each {
	template.sendBody("direct:start", "test${it}")
}

println "stop"

ctx.stop()


