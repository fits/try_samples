/*
 * Gretty sample
 *   Groovy 1.8 で実行する必要あり
 *   Groovy 2.0 では ExceptionInitilizerError が発生する
 *
 */
@GrabResolver(name = 'gretty', root = 'http://groovypp.artifactoryonline.com/groovypp/libs-releases-local/')
@Grab('org.mbte.groovypp:gretty:0.4.302')
import org.mbte.gretty.httpserver.*

GrettyServer server = []

server.groovy = [
	localAddress: new InetSocketAddress('localhost', 8080),
	defaultHandler: {
		response.redirect '/'
	},
	'/': {
		get {
			response.html = 'hello'
		}
	},
	// '/:name' より先に定義する必要あり
	'/tp/:title': {
		get {
			response.html = template('sample.gpptl', [
				title: request.parameters['title']
			])
		}
	},
	'/:name': {
		get {
			response.text = "param : ${request.parameters['name']}"
		}
	}
]

server.start()
