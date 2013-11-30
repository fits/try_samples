
def eb = vertx.eventBus

eb.send('sample', "sample msg 111") { reply ->
	println "*** recieved: ${reply.body}"
}
