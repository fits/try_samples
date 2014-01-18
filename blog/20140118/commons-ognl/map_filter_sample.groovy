@GrabResolver('http://repository.apache.org/snapshots/')
@Grab('org.apache.commons:commons-ognl:4.0-SNAPSHOT')
import org.apache.commons.ognl.Ognl
import org.apache.commons.ognl.ClassResolver

def data = new Order()
data.lines << new OrderLine('1', 100)
data.lines << new OrderLine('2', 200)
data.lines << new OrderLine('3', 300)

try {
	// (1) DefaultClassResolver が原因でエラーが発生
	println Ognl.getValue('lines.{? #this.code == "2" }', data)
} catch (e) {
	println '(1) ' + e
	//e.printStackTrace()
}

println '-----------------------'

// エラー回避のために自前の ClassResolver を使用
def ctx = Ognl.createDefaultContext(null, { String className, Map<String, Object> context ->
	Class.forName(className)
} as ClassResolver)


// (2) マッピング
println '(2) ' + Ognl.getValue('lines.{ #this.price > 100 }', ctx, data)

// (3) フィルタリング
println '(3) ' + Ognl.getValue('lines.{? #this.price > 100 }', ctx, data)

// (4) マッチした最初の要素
println '(4) ' + Ognl.getValue('lines.{^ #this.price > 100 }', ctx, data)

// (5) マッチした最後の要素
println '(5) ' + Ognl.getValue('lines.{$ #this.price > 100 }', ctx, data)

// (6) OGNL式をコンパイルして使用
def exprNode = Ognl.compileExpression(ctx, null, 'lines.{? #this.code not in {"2", "4"} }')
println '(6) ' + Ognl.getValue(exprNode, data)
