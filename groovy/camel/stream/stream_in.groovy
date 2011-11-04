
@GrabResolver(name = "Apache Camel SNAPSHOTS", root = "https://repository.apache.org/content/repositories/snapshots/")
@Grab("org.apache.camel:camel-core:2.9.0-SNAPSHOT")
@Grab("org.apache.camel:camel-stream:2.9.0-SNAPSHOT")
import org.apache.camel.Processor
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder

ctx = new DefaultCamelContext()

ctx.addRoutes(new RouteBuilder() {
	void configure() {
		from("stream:in")
			.split(body().tokenize("\n"))
			.process({println it.in.body} as Processor)
	}
})

ctx.start()

ctx.shutdown()
