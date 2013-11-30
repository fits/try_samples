
def eb = vertx.eventBus

eb.registerHandler('sample') { msg ->
	println "*** receive : ${msg.body}"

	msg.reply 'received'
}

eb.send('sample', "message1") { reply ->
	println "*** reply: ${reply.body}"
}