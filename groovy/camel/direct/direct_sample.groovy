@Grab('org.apache.camel:camel-core:2.19.2')
@Grab('org.slf4j:slf4j-nop:1.7.25')
import org.apache.camel.main.Main
import org.apache.camel.builder.RouteBuilder

class SampleRoute extends RouteBuilder {
	void configure() {
		from('direct:test').process { println it.in.body }
	}
}

def main = new Main()

main.addRouteBuilder(new SampleRoute())

main.start()

def tpl = main.camelTemplate

(1..10).each {
	tpl.sendBody('direct:test', "data${it}")
}

main.shutdown()