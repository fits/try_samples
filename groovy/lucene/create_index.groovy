@Grab('org.apache.lucene:lucene-core:5.4.1')
@Grab('org.apache.lucene:lucene-analyzers-common:5.4.1')
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.store.FSDirectory

import java.nio.file.Paths

def dataDir = args[0]
def fileName = args[1]

def analyzer = new StandardAnalyzer()

def dir = FSDirectory.open(Paths.get(dataDir))

def iwc = new IndexWriterConfig(analyzer)
//iwc.openMode = OpenMode.CREATE

new IndexWriter(dir, iwc).withCloseable { writer ->

	def f = new File(fileName)

	f.eachLine {
		def line = it.trim()

		if (line) {
			def doc = new Document()

			doc.add(new StringField('path', f.toString(), Field.Store.YES))
			doc.add(new TextField('message', line, Field.Store.YES))

			writer.addDocument(doc)
		}
	}
}
