
if (args.length < 3) {
	println "<dir> <ext> <words1> ..."
	return
}

def listFiles = { cond, String dirName ->
	def res = []

	new File(dirName).eachFileRecurse {
		if (cond(it)) {
			res << it
		}
	}
	res
}

def wordContains = { List inWords, String target ->
	inWords.inject(false) { acc, val ->
		if (!acc) {
			acc = target.contains(val)
		}
		acc
	}
}

def check = { ext, cond, f ->
	f.name.endsWith(".${ext}") && cond(f)
}
def lower = { File f -> 
	f.text.toLowerCase()
}

def dir = args[0]
def ext = args[1]
List words = args.drop(1).tail()

def checklogic = check.curry(ext, lower >> wordContains.curry(words))

def files = listFiles.curry(checklogic) >> {
	it.collect { it.path }.unique().sort()
}

files(dir).each {
	println it
}
