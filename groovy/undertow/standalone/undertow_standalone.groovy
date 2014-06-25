@Grab('io.undertow:undertow-core:1.1.0.Beta3')
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.util.Headers

def server = Undertow.builder().addListener(8080, 'localhost').setHandler( { ex ->
	println ex.dump()

	ex.responseHeaders.put(Headers.CONTENT_TYPE, 'text/plain')
	ex.responseSender.send('sample')

} as HttpHandler ).build()

server.start()
