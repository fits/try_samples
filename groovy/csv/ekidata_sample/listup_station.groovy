
def prefMap = new File("m_pref.csv") readLines() collectEntries {
	def items = it.split(",")
	[items[0], items[1]]
}

def lines = new File("m_station.csv") readLines()
lines.remove(0)

def list = lines.collect {it.split(",")} groupBy {
	[it[9], prefMap[it[10]], it[5]]
} sort {a, b -> b.value.size <=> a.value.size} entrySet() asList()[0..9]

list.each {
	println "${it.key[0]}‰w (${it.key[1]}) : ${it.value.size}"
}

