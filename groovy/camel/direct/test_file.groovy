
import org.apache.camel.Processor
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder

class SampleRoute extends RouteBuilder {
	void configure() {

		from("file:test").process({println "file - $it"} as Processor).to("direct:created")

		from("direct:created").process({
			println "---------------------------"
			println "isTransacted: ${it.transacted}"
			println "in = header: ${it.in.headers}, body: ${it.in.body}, attachment: ${it.in.attachmentNames}"

			println "--- ${it.file} ---"
			//ファイルの出力
			println it.file.text
			//以下でも可
			println it.in.body.text

		} as Processor)

	}
}

ctx = new DefaultCamelContext()

ctx.addRoutes(new SampleRoute())

template = ctx.createProducerTemplate()

ctx.start()

println "start Camel"

System.in.read()

ctx.stop()

