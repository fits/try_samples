def wsMap = [:] as java.util.concurrent.ConcurrentHashMap

vertx.createHttpServer().websocketHandler { ws ->
	ws.dataHandler { data ->
		wsMap.each { k, v ->
			v.writeTextFrame(data.toString())
		}
	}

	ws.closedHandler {
		wsMap.remove(ws.textHandlerID)
		println "closed : ${ws.textHandlerID}"
	}

	println "connect : ${ws.textHandlerID}"
	wsMap.put(ws.textHandlerID, ws)

}.listen 8080

println "server started ..."
