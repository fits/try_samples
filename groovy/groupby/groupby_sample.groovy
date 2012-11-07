
class Data {
	String id
	String type
	int value
}

def dataList = [
	new Data(id: 1, type: 'A', value: 1),
	new Data(id: 2, type: 'A', value: 2),
	new Data(id: 3, type: 'B', value: 3),
	new Data(id: 4, type: 'A', value: 4),
	new Data(id: 5, type: 'C', value: 5),
	new Data(id: 6, type: 'B', value: 6)
]

def res = dataList.groupBy { it.type }

res.each {k, v ->
	println "--- $k ---"
	v.each { println it.id }
}
