@Grab('net.sf.jtidy:jtidy:r938')
@Grab('xalan:xalan:2.7.2')
@GrabExclude('xml-apis#xml-apis;1.3.04')
import org.w3c.tidy.Tidy
import org.apache.xpath.XPathAPI

def file = args[0]
def expr = args[1]

def tidy = new Tidy()

new File(file).withInputStream { f ->
	def doc = tidy.parseDOM(f, null)

	def res = XPathAPI.eval(doc, expr)

	println "--- result ---"

	res.nodelist().each {
		println "name: ${it.nodeName}, value: ${it.nodeValue}"
	}
}
