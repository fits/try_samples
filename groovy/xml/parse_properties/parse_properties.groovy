
def parser = new XmlSlurper()
parser.setFeature 'http://apache.org/xml/features/disallow-doctype-decl', false

def doc = parser.parse(new File(args[0]))

doc.entry.each {
	println "${it.@key}: ${it}"
}
