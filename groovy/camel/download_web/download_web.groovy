
@GrabResolver(name = "Apache Camel SNAPSHOTS", root = "https://repository.apache.org/content/repositories/snapshots/")
@Grab("org.apache.camel:camel-core:2.9.0-SNAPSHOT")
@Grab("org.apache.camel:camel-http:2.9.0-SNAPSHOT")
@Grab("org.apache.camel:camel-stream:2.9.0-SNAPSHOT")
//@Grab("org.slf4j:slf4j-simple:1.6.3")
//@Grab("org.slf4j:slf4j-jdk14:1.6.3")
@Grab("org.slf4j:slf4j-nop:1.6.3")
import org.apache.camel.Processor
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder

def dir = args[0]

ctx = new DefaultCamelContext()

ctx.addRoutes(new RouteBuilder() {
	void configure() {
		//例外発生時の処理
		onException(Exception.class)
			.handled(true)
			.process({
				println "failed: ${it.in.body}"
			} as Processor)

		from("stream:in")
			.split(body().tokenize())
			.process({
				def url = it.in.body
				//接続先URLの設定
				it.in.setHeader(Exchange.HTTP_URI, url)
				//出力ファイル名の設定
				it.in.setHeader(Exchange.FILE_NAME, url.split('/').last())
			} as Processor)
			.to("http://localhost/")
			.to("file:${dir}")
			.process({
				println "downloaded: ${it.in.getHeader(Exchange.HTTP_URI)} => ${it.in.getHeader(Exchange.FILE_NAME)}"
			} as Processor)
	}
})

ctx.start()

ctx.stop()
