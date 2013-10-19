package fits.sample

import org.vertx.groovy.core.http.RouteMatcher
import net.arnx.jsonic.JSON

class RoutingFactory {
	final static String CONTENTS_DIR = 'web'

	static Closure create() {
		def rm = new RouteMatcher()

		rm.get '/item', { req ->
			def res = req.response

			res.putHeader('Content-Type', 'application/json')

			res.end JSON.encode([
				new Item('a1', 'test1'),
				new Item('b2', 'test2')
			])
		}

		rm.get '.*', { req -> 
			def path = req.path

			if (path.split('/|\\\\').contains('..')) {
				req.response.setStatusCode(404).end()
			}
			else {
				if (path == '/') {
					path += 'index.html'
				}
				else if (!path.startsWith('/')) {
					path = "/${path}"
				}

				req.response.sendFile("${CONTENTS_DIR}${path}")
			}
		}

		rm.asClosure()
	}
}
