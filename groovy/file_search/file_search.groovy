
if (args.length < 3) {
	println "<dir> <file extension> <words>"
	return
}

def listFiles = { dirName, cond ->
	def res = []

	new File(dirName).eachFileRecurse {
		if (cond(it)) {
			res << it
		}
	}
	res
}

listFiles(args[0]) {

	it.name.endsWith(".${args[1]}") &&
		it.text.toLowerCase().contains(args[2].toLowerCase())

}.collect { it.name }.unique().sort().each {
	println it
}
