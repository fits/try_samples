@Grab('org.apache.lucene:lucene-core:6.2.1')
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.store.FSDirectory
import java.nio.file.Paths

def dir = FSDirectory.open(Paths.get(args[0]))

DirectoryReader.open(dir).withCloseable { reader ->
	println "numDocs = ${reader.numDocs()}"

	(0..<reader.numDocs()).each {
		def doc = reader.document(it)

		println "---------- doc: ${it} ----------"

		doc.fields.each { f -> 
			def value = f.binaryValue()? f.binaryValue().utf8ToString(): f.stringValue()

			println "<field> name=${f.name}, value=${value}, class=${f.class}"
		}
	}
}