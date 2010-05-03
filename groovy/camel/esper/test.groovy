
import org.apache.camel.Processor
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder

class SampleRoute extends RouteBuilder {
	void configure() {
		println "--- call configure()"

		from("direct:start").to("esper:test")

		from("esper:test?eql=select * from TestEvent(value >= 5)")
			.process({println "process ${it.in.body.name}"} as Processor)
	}
}

class TestEvent {
	String name
	int value
}

ctx = new DefaultCamelContext()
ctx.addRoutes(new SampleRoute())

template = ctx.createProducerTemplate()

println "start"

ctx.start()

println "end start"

(1..10).each {
	template.sendBody("direct:start", new TestEvent(name: "test${it}", value: it))
}

println "stop"

ctx.stop()


