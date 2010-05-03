
import org.apache.camel.Processor
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder

class SampleRoute extends RouteBuilder {
	void configure() {
		from("direct:start").to("esper:test")

		from("esper:test?eql=select count(*) as counter from TestEvent.win:time_batch(1 sec)").process({println "*** 1 counter:${it.in.body.counter}"} as Processor)

		from("esper:test?eql=select name, count(*) as counter from TestEvent.win:time(1 sec)").process({println "--- 2 counter:${it.in.body.name} - ${it.in.body.counter}"} as Processor)

		from("esper:test?eql=select name, count(*) as counter from TestEvent.win:length(3)").process({println "=== 3 counter:${it.in.body.name} - ${it.in.body.counter}"} as Processor)

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

(1..10).each {
	template.sendBody("direct:start", new TestEvent(name: "test${it}", value: it))
	Thread.sleep(300);
}

Thread.sleep(1000);

ctx.stop()

println "stop"
