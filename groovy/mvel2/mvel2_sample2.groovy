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

def c1 = MVEL.compileExpression('point > 5')
println MVEL.executeExpression(c1, obj)

def c2 = MVEL.compileExpression('point > 15')
println MVEL.executeExpression(c2, obj)

def c3 = MVEL.compileExpression('lines')
println MVEL.executeExpression(c3, obj)

def c4 = MVEL.compileExpression('lines[0].code == "123"')
println MVEL.executeExpression(c4, obj)
