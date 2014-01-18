@GrabResolver('http://repository.apache.org/snapshots/')
@Grab('org.apache.commons:commons-ognl:4.0-SNAPSHOT')
import org.apache.commons.ognl.Ognl
import org.apache.commons.ognl.ClassResolver

def data = new Order()
data.lines << new OrderLine('1', 100)
data.lines << new OrderLine('2', 200)
data.lines << new OrderLine('3', 300)

def ctx = Ognl.createDefaultContext(null, { String className, Map<String, Object> context ->
	Class.forName(className)
} as ClassResolver)

def foldOgnl = '''
#fold = :[
	#this[2].size() > 0 ? #fold({ #this[0], #this[0]({ #this[1], #this[2][0] }), #this[2].subList(1, #this[2].size()) }) : #this[1]
],
#fold({ :[ #this[0] + #this[1].price ], 0, lines })
'''

// 畳み込みを使った合計処理 0 + 100 + 200 + 300
println Ognl.getValue(foldOgnl, ctx, data)

// 変数を使った合計処理
println Ognl.getValue('#v = 0, lines.{ #v = #v + #this.price }, #v', ctx, data)
