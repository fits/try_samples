@Grab('io.reactivex:rxnetty:0.4.6')
import static io.netty.handler.codec.http.HttpMethod.*
import static io.netty.handler.codec.http.HttpResponseStatus.*

import io.reactivex.netty.RxNetty
import io.reactivex.netty.protocol.http.server.RequestHandler

import io.netty.handler.codec.http.DefaultCookie

def printRequest = { req ->
	println "method: ${req.method}, path: ${req.path}, cookies: ${req.cookies}"
}

def counter = 0

def server = RxNetty.newHttpServerBuilder(8080, { req, res ->
	printRequest req

	if (req.httpMethod == GET) {
		res.writeStringAndFlush("path: ${req.path}, cookies: ${req.cookies}")
	}
	else if (req.httpMethod == POST) {
		res.addCookie(new DefaultCookie('id', counter++ as String))

		res.status = SEE_OTHER
		res.headers.set('Location', '/show')

		res.flush()
	}
} as RequestHandler).build()

println 'started ...'

server.startAndWait()
