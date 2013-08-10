@Grab('org.mvel:mvel2:2.1.6.Final')
import org.mvel2.MVEL

class Item {
	def code
}

class Sample {
	def name
	def point
	def lines = []
}

def obj = new Sample(name: 'sample', point: 10)
obj.lines << new Item(code: '123')

println MVEL.eval('point > 5', obj)

println MVEL.eval('point > 15', obj)

println MVEL.eval('lines', obj)

println MVEL.eval('lines[0].code == "123"', obj)
