
def splitEachByte = { fileName, size ->
	def res = []

	new File(fileName).eachByte(size) { buf, len ->
		// buf は同一オブジェクトが再利用される点に注意
		res << Arrays.copyOf(buf, len)
	}
	res
}

def writeFile = { fileName, list ->
	new File(fileName).withOutputStream { output ->
		list.each { output.write it }
	}
}

def list = splitEachByte(args[0], 10).split {
	it.length > 4 && new String(it, 4, 1) == '0'
}

writeFile('g1.dat', list.head())
writeFile('g2.dat', list.last())
