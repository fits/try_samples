
@GrabResolver(name = "Apache Camel SNAPSHOTS", root = "https://repository.apache.org/content/repositories/snapshots/")
@Grab("org.apache.camel:camel-core:2.9.0-SNAPSHOT")
@Grab("org.apache.camel:camel-http:2.9.0-SNAPSHOT")
import org.apache.camel.Processor
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder

ctx = new DefaultCamelContext()

ctx.addRoutes(new RouteBuilder() {
	void configure() {
		from("direct:start")
			.to("http://www.google.co.jp/")
			.process({println it.in.body.text} as Processor)
			.end()
	}
})


template = ctx.createProducerTemplate()

println "start"

ctx.start()

template.sendBody("direct:start", null)
