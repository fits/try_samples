@Grab('org.apache.lucene:lucene-core:6.2.1')
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.store.FSDirectory
import java.nio.file.Paths

def dir = FSDirectory.open(Paths.get(args[0]))

DirectoryReader.open(dir).withCloseable { reader ->

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
					println "term=${termsEnum.term().utf8ToString()}, freq=${termsEnum.docFreq()}"
				}
			} catch(e) {
			}
		}
	}
}