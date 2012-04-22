
NetworkInterface.networkInterfaces.each {
	println "***** ${it.displayName} *****"
	println "name: ${it.name}"
	println "loopback: ${it.loopback}"
	println "p2p : ${it.pointToPoint}"
	println "MTU : ${it.getMTU()}"
	println "virtual : ${it.virtual}"
	println "supportsMulticast : ${it.supportsMulticast()}"

	println "--- inet address ---"
	it.inetAddresses.each {
		println it
	}

	println "--- interface address ---"
	it.interfaceAddresses.each {
		println it
	}
	
	println ""
}

