
vertx.createHttpServer().requestHandler {req ->
	req.response.end "hello"

}.listen 8080
