@Grab('io.undertow:undertow-core:1.3.0.Beta9')
import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.util.Headers
import io.undertow.util.HttpString

def size = args[0] as int

def server = Undertow.builder().addListener(8081, 'localhost').setHandler( { ex ->
	ex.responseHeaders
		.put(Headers.CONTENT_TYPE, 'text/plain')
		.put(new HttpString('TEST'), 't' * size)

	ex.responseSender.send('sample')

} as HttpHandler ).build()

server.start()