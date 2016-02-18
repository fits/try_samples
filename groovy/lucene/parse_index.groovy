@Grab('org.apache.lucene:lucene-core:5.4.1')
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.store.FSDirectory

import java.nio.file.Paths

def dataDir = args[0]

def dir = FSDirectory.open(Paths.get(dataDir))

DirectoryReader.open(dir).withCloseable { reader ->
	println "numDocs = ${reader.numDocs()}"

	// Document
	(0..<reader.numDocs()).each {
		def doc = reader.document(it)

		println "----- doc: ${it} -----"

		doc.fields.each { f -> 
			def value = f.binaryValue()? f.binaryValue().utf8ToString(): f.stringValue()

			println "<field> name=${f.name}, value=${value}, class=${f.class}"
		}
	}

	println ''

	// Term
	reader.leaves().each { ctx ->
		def leafReader = ctx.reader()

		leafReader.fields().each { name ->
			def termsEnum = leafReader.terms(name).iterator()

			def buf = null

			try {
				while((buf = termsEnum.next()) != null) {
					println "<term: ${name}> ${buf.utf8ToString()}, freq=${termsEnum.docFreq()}, total=${termsEnum.totalTermFreq()}"
				}
			} catch(e) {}
		}
	}
}