@Grab('net.sf.opencsv:opencsv:2.3')
import au.com.bytecode.opencsv.bean.*

class SampleData {
	String name
	int point
}

def data = '''name,point
"ユーザー1",100
"ユーザー2",80
'''

def reader = new StringReader(data)

def strategy = new HeaderColumnNameMappingStrategy<SampleData>()
strategy.type = SampleData.class

def list = new CsvToBean().parse(strategy, reader)

list.each {
	println "${it.name}, ${it.point}"
}

