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

// 100 + 200 + 300 = 600
println Ognl.getValue("#v = 0, lines.{ #v = #v + #this.price }, #v", ctx, data)

// 1 + 2 = 3
println Ognl.getValue("#func = :[ #this[0] + #this[1] ], #func({ 1, 2 })", ctx, data)

// 2 + 3 = 5
println Ognl.getValue("#func = :[ #this[0](#this[1]) ], #f = :[ #this + 3 ], #func({ #f, 2 })", ctx, data)

// 2 + 3 = 5
println Ognl.getValue("#func = :[ #this[0](#this[1]) ], #func({ :[ #this + 3 ], 2 })", ctx, data)


println Ognl.getValue('#fold = :[ #this[2].size() > 0 ? #fold({ #this[0], #this[0]({ #this[1], #this[2][0] }), #this[2].subList(1, #this[2].size()) }) : #this[1] ], #fold({ :[ #this[0] + #this[1] ], 0, {100, 200, 300} })', ctx, data)

println Ognl.getValue('#fold = :[ #this[2].size() > 0 ? #fold({ #this[0], #this[0]({ #this[1], #this[2][0] }), #this[2].subList(1, #this[2].size()) }) : #this[1] ], #fold({ :[ #this[0] + #this[1].price ], 0, lines })', ctx, data)


println Ognl.getValue('#fold = :[ #this["values"].size() > 0 ? #fold( #{ "func" : #this["func"], "acc" : #this["func"]({ #this["acc"], #this["values"][0] }), "values" : #this["values"].subList(1, #this["values"].size()) }) : #this["acc"] ], #f = :[ #this[0] + #this[1].price ], #fold( #{ "func" : #f, "acc" : 0, "values" : lines })', ctx, data)

println Ognl.getValue('#fold = :[ #this["values"].size() > 0 ? #fold( #{ "func" : #this["func"], "acc" : #this["func"]({ #this["acc"], #this["values"][0] }), "values" : #this["values"].subList(1, #this["values"].size()) }) : #this["acc"] ], #fold( #{ "func" : :[ #this[0] + #this[1].price ], "acc" : 0, "values" : lines })', ctx, data)

