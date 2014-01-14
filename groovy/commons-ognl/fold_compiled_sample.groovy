@GrabResolver('http://repository.apache.org/snapshots/')
@Grab('org.apache.commons:commons-ognl:4.0-SNAPSHOT')
import org.apache.commons.ognl.Ognl
import org.apache.commons.ognl.ClassResolver

import groovy.transform.*

class Sample {
	def lines = []
}

@Immutable
class Item {
	int code
	BigDecimal price = BigDecimal.ZERO
}

def data = new Sample()
data.lines << new Item(code: 1, price: 100)
data.lines << new Item(code: 2, price: 200)
data.lines << new Item(code: 3, price: 300)

def ctx = Ognl.createDefaultContext(null, { String className, Map<String, Object> context ->
	Class.forName(className)
} as ClassResolver)

def expr1 = Ognl.compileExpression(ctx, null, '#v = 0, lines.{ #v = #v + #this.price }, #v')

def expr2 = Ognl.compileExpression(ctx, null, '#fold = :[ #this[2].size() > 0 ? #fold({ #this[0], #this[0]({ #this[1], #this[2][0] }), #this[2].subList(1, #this[2].size()) }) : #this[1] ], #fold({ :[ #this[0] + #this[1].price ], 0, lines })')

println Ognl.getValue(expr1, data)
println Ognl.getValue(expr2, data)

println '-----'

def data2 = new Sample()
data2.lines << new Item(code: 1, price: 1500)
data2.lines << new Item(code: 2, price: 2300)

println Ognl.getValue(expr1, data2)
println Ognl.getValue(expr2, data2)

