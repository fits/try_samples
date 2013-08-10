
if (args.length < 3) {
	println "<dir> <file extension> <words1>..."
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

def words = args.collect { it.toLowerCase() }.drop(1).tail()

listFiles(args[0]) { f ->

	f.name.endsWith(".${args[1]}") &&
	words.inject(false) { acc, val ->
		if (!acc) {
			acc = f.text.toLowerCase().contains(val)
		}
		acc
	}

}.collect { it.path }.unique().sort().each {
	println it
}
