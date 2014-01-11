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

println Ognl.getValue("lines.{? #this.code == 2 }", ctx, data)

def expr = Ognl.compileExpression(ctx, null, 'lines.{? #this.code not in {"1"} }')

println Ognl.getValue(expr, data)


/* 下記はエラーが発生
 * 
 * 原因は DefaultClassResolver が SystemClassLoader を使って
 * javassist.ClassPool ロードしようとして失敗する事
 *
 * Caused by: java.lang.ClassNotFoundException:
 *   Unable to resolve class: javassist.ClassPool
 */
// println Ognl.getValue("lines.{? #this.code == 2 }", data)

