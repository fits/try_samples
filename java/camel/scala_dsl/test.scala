
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.scala.dsl.builder.RouteBuilder

object Test {
	class TestRouteBuilder extends RouteBuilder {
		val proc = (exchange: Exchange) => {
			exchange.in match {
				case "test1" => exchange.in = "hello test1"
				case "test3" => exchange.in = "hello2 test3"
				case _ => 
			}

			println(exchange.in)
		}

		"direct:a" process(proc) to ("mock:b")
	}

	def main(args: Array[String]) {
		val ctx = new DefaultCamelContext()
		ctx.addRoutes(new TestRouteBuilder())

		val template = ctx.createProducerTemplate()

		ctx.start()

		(1 until 5).foreach (num =>
			template.sendBody("direct:a", "test" + num)
		)

		ctx.stop()
	}
}
