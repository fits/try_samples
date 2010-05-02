
import groovy.xml.StreamingMarkupBuilder

def xml = "<root><a><value>123</value></a><b><value>test</value></b></root>"

def doc = new XmlSlurper().parseText(xml)

println doc.getClass()

//c —v‘f‚Ì’Ç‰Á
doc.appendNode {
	c("abc")
}

//‘®«‚Ì’Ç‰Á
doc.b.@id = "no1"

//—v‘f‚Ì’uŠ·
doc.b.value.replaceNode { n ->
	item(type: "data") {
		value("test data")
	}
}

//a —v‘f‚Ìíœ
doc.a.replaceNode {}

def builder = new StreamingMarkupBuilder()

//•¶š—ñ‚ÅXMLæ“¾
def xmlString = builder.bind{
	mkp.yield doc
}

println xmlString
