@Grab('org.apache.lucene:lucene-core:6.2.1')
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.store.FSDirectory

import java.nio.file.Paths

def dataDir = args[0]

def dir = FSDirectory.open(Paths.get(dataDir))

DirectoryReader.open(dir).withCloseable { reader ->
	println '[Document]'
	println "numDocs = ${reader.numDocs()}"

	// Document
	(0..<reader.numDocs()).each {
		def doc = reader.document(it)

		println "---------- doc: ${it} ----------"

		doc.fields.each { f -> 
			def value = f.binaryValue()? f.binaryValue().utf8ToString(): f.stringValue()

			println "<field> name=${f.name}, value=${value}, class=${f.class}"
		}
	}

	println ''

	println '[Leaves]'
	reader.leaves().each { ctx ->
		def leafReader = ctx.reader()

		println "---------- leaf: ${leafReader} ----------"

		leafReader.getFieldInfos().each { fi ->
			println "<fieldInfo> name: ${fi.name}, valueType: ${fi.docValuesType}, indexOptions: ${fi.indexOptions}"
		}

		leafReader.fields().each { name ->
			def termsEnum = leafReader.terms(name).iterator()

			println ''
			println "===== <term> name=${name} ====="

			try {
				while(termsEnum.next() != null) {
					println "term=${termsEnum.term().utf8ToString()}, freq=${termsEnum.docFreq()}, total=${termsEnum.totalTermFreq()}"
				}
			} catch(e) {
			}
		}
	}
}