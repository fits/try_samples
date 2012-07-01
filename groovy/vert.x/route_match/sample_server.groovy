import org.vertx.groovy.core.http.RouteMatcher

def rm = new RouteMatcher()

rm.get '/sample', { req ->
	req.response.end 'test data'
}

rm.get '/sample/:id', { req ->
	req.response.end "data : ${req.params['id']}"
}

vertx.createHttpServer().requestHandler(rm.asClosure()).listen 8080
