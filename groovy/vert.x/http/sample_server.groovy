
vertx.createHttpServer().requestHandler {req ->
	req.response.end "test data"

}.listen 8080
