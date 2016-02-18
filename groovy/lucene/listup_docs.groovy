@Grab('org.apache.lucene:lucene-core:5.4.1')
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.store.FSDirectory

import java.nio.file.Paths

def dataDir = args[0]

DirectoryReader.open(FSDirectory.open(Paths.get(dataDir))).withCloseable { reader ->
	println reader.numDocs()

	(0..<reader.numDocs()).each {
		println reader.document(it)
	}
}