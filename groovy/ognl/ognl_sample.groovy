@GrabResolver('http://repository.apache.org/snapshots/')
@Grab('org.apache.commons:commons-ognl:4.0-SNAPSHOT')
import org.apache.commons.ognl.Ognl

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

println Ognl.getValue('point > 5', obj)

println Ognl.getValue('point > 15', obj)

println Ognl.getValue('lines', obj)

/* ˆÈ‰º‚Ìˆ—‚ÍƒGƒ‰[‚ª”­¶
*
*   java.lang.IllegalArgumentException: Javassist library
*     is missing in classpath! Please add missed dependency!
*/
println Ognl.getValue('lines[0]', obj)
