@Grab('io.reactivex:rxnetty-http:0.5.2')
@Grab('org.slf4j:slf4j-nop:1.7.25')
import io.reactivex.netty.protocol.http.server.HttpServer
import java.nio.charset.Charset

import static rx.Observable.*

def port = args[0] as int

def server = HttpServer.newServer(port).start { req, res ->

	println '----- info -----'
	println "method = ${req.httpMethod}"
	println "uri = ${req.uri}"
	println "queryString = ${req.rawQueryString}"

	println '----- headers -----'
	req.headerNames.each {
		println "${it}: ${req.getHeader(it)}"
	}

	req.content.subscribe {
		println '----- request body -----'
		println it.toString(Charset.defaultCharset())
	}

	res.writeString(just('sample'))
}

println 'started ...'

server.awaitShutdown()
