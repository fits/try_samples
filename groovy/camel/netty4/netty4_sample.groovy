@Grab('org.apache.camel:camel-netty4-http:2.19.2')
@Grab('org.slf4j:slf4j-nop:1.7.25')
import org.apache.camel.main.Main
import org.apache.camel.builder.RouteBuilder

class SampleRoute extends RouteBuilder {
	void configure() {

		restConfiguration()
			.host("localhost")
			.port(8080)

		rest('/sample')
			.post()
			.to('direct:post')

		from('direct:post')
			.process {
				println it.in.getBody(String)
				it
			}
			.transform().constant('ok')
	}
}

def main = new Main()

main.addRouteBuilder(new SampleRoute())

main.start()

println 'press enter to stop'

System.in.read()

main.shutdown()