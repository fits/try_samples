
def xmlFile = args[0]
def trgId = args[1]

def parser = new XmlSlurper(false, false, true)

parser.setFeature('http://apache.org/xml/features/nonvalidating/load-external-dtd', false)

def doc = parser.parse(new File(xmlFile))

doc.children().findAll { it.@id == trgId }.each {
	println it
}
