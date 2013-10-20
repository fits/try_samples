package fits.sample

import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.java.core.json.impl.Json

class RoutingMatcherFactory {
	private final static String CONTENTS_DIR = 'web'

	static Closure create() {
		def rm = new RouteMatcher()

		rm.get '/item', { req ->
			def res = req.response

			res.putHeader('Content-Type', 'application/json')

			res.end Json.encode([
				[id: 'a1', name: 'test1'],
				[id: 'b2', name: 'サンプル2']
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
