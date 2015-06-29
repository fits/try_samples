import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.*

def builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
def doc = builder.parse(new File(args[0]))

def xpath = XPathFactory.newInstance().newXPath()

def res = xpath.evaluate(args[1], doc, XPathConstants.NODESET)

res.each {
	println it
}
