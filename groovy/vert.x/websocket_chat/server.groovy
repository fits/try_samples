
def wsList = new java.util.concurrent.CopyOnWriteArrayList()

vertx.createHttpServer().websocketHandler { ws ->
	wsList.add(ws)

	ws.dataHandler { data ->
		wsList.each {
			it.writeTextFrame(data.toString())
		}
	}

	ws.closedHandler {
		wsList.remove(ws)
		println "*** closed : ${ws}"
	}

}.listen 8080

println "server started ..."
