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
obj.lines << new Item(code: 'xyz')
obj.lines << new Item(code: 'abc')

def exp = '''
codeList = (code in obj.lines);
codeList contains 'xyz'
'''

// true
println MVEL.eval(exp, [obj: obj])

def exp2 = '''
codeList = (code in obj.lines);
codeList contains 'ab'
'''

// false
println MVEL.eval(exp2, [obj: obj])

