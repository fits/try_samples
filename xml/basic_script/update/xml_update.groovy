
import groovy.xml.StreamingMarkupBuilder

def doc = new XmlSlurper().parse(new File(args[0]))

//—v‘f‚Ì’Ç‰Á <data id="3"><details>added</details></data>
doc.appendNode {
	data(id: "3") {
		details("added")
	}
}



//•¶š—ñ‚ÅXML‚ğæ“¾‚µ‚Äo—Í
println new StreamingMarkupBuilder().bind{
	mkp.yield doc
}
