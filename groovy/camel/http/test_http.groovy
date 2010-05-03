
import org.apache.camel.Processor
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder

class SampleRoute extends RouteBuilder {
	void configure() {

		from("jetty:http://localhost/test").to("direct:response")

		from("direct:response").process({

			def req = it.in.getBody(javax.servlet.http.HttpServletRequest.class)
			def id = req.getParameter("id")

			it.out.setBody("<html><body><h1>id=${id}</h1></body></html>", String.class)
			//以下のような記述ではファイルが出力されないため注意
			//it.out.body = "<html><body><h1>id=${id}</h1></body></html>"
		} as Processor).to("file:logs")

		from("file:logs").process({
			//ファイル名とファイル内容を出力
			println "${it.file.name} : ${it.file.text}"
		} as Processor)
	}
}

ctx = new DefaultCamelContext()

ctx.addRoutes(new SampleRoute())

ctx.start()

println "start Camel"

System.in.read()

ctx.stop()

