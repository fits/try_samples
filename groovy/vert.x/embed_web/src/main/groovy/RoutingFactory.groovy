package fits.sample

import org.vertx.groovy.core.http.RouteMatcher

class RoutingFactory {
	final static String CONTENTS_DIR = 'web'

	static Closure create() {
		def rm = new RouteMatcher()

		rm.get '/item', { req ->
			req.response.end 'item1,item2'
		}

		rm.get '.*', { req -> 
			def path = "${CONTENTS_DIR}${req.path}"

			if (path.split('/|\\\\').contains('..')) {
				req.response.setStatusCode(404).end()
			}
			else {
				req.response.sendFile(path)
			}
		}

		rm.asClosure()
	}
}
