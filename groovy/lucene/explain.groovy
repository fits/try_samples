@Grab('org.apache.lucene:lucene-core:5.4.1')
@Grab('org.apache.lucene:lucene-analyzers-kuromoji:5.4.1')
@Grab('org.apache.lucene:lucene-queryparser:5.4.1')
import org.apache.lucene.analysis.ja.JapaneseAnalyzer
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.queryparser.classic.QueryParser

import java.nio.file.Paths

def dataDir = args[0]
def searchWord = args[1]
def docIndex = args[2] as int

DirectoryReader.open(FSDirectory.open(Paths.get(dataDir))).withCloseable { reader ->
	def searcher = new IndexSearcher(reader)

	def analyzer = new JapaneseAnalyzer()

	def parser = new QueryParser('message', analyzer)

	def query = parser.parse(searchWord)

	def exp = searcher.explain(query, docIndex)

	println exp
}