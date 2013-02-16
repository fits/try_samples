import org.vertx.groovy.core.http.RouteMatcher
import org.vertx.java.core.json.impl.Json

def rm = new RouteMatcher()
def list = [] as java.util.concurrent.CopyOnWriteArrayList

rm.get '/user', { req ->
	println "get /user"

    def res = req.response

	println list

    res.putHeader('Content-Type', 'application/json')
    res.end Json.encode(list)
}

rm.post '/user', { req ->
	println "post /user"

    req.bodyHandler {
        // JSON を Map へデコード
        def data = Json.decodeValue(it.toString(), Map)
        println data

		data.updateDate = new Date()

		list.add(data)

        req.response.end Json.encode(data)
    }
}

rm.delete '/user/:id', { req ->
	def groupId = req.params['id']

	println "delete /user/${groupId}"

	def item = list.find {
		it.groupId == groupId
	}

	if (item) {
		list.remove(item)
	}
	req.response.end Json.encode(item)
}

rm.get '/:file', { req ->
	req.response.end(new File(req.params['file']).getText('UTF-8'))
}

vertx.createHttpServer().requestHandler(rm.asClosure()).listen 8080

println "server started ..."

