
def dataList = [
	[id: 1, type: 'A', category: 'T', value: 1],
	[id: 2, type: 'A', category: 'S', value: 2],
	[id: 3, type: 'B', category: 'T', value: 3],
	[id: 4, type: 'A', category: 'S', value: 4],
	[id: 5, type: 'C', category: 'T', value: 5],
	[id: 6, type: 'B', category: 'T', value: 6]
]

def res = dataList.groupBy { [it.type, it.category] }

res.each {k, v ->
	println "--- $k ---"
	v.each { println it.id }
}
