
import groovy.xml.StreamingMarkupBuilder

def doc = new XmlSlurper().parse(new File(args[0]))

//—v‘f‚Ì’Ç‰Á <data id="3"><details>added</details></data>
doc.appendNode {
	data(id: "3") {
		details("added")
	}
}



def builder = new StreamingMarkupBuilder()

//•¶Žš—ñ‚ÅXMLŽæ“¾
def xmlString = builder.bind{
	mkp.yield doc
}

println xmlString

