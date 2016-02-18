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

def reader = DirectoryReader.open(FSDirectory.open(Paths.get(dataDir)))
def searcher = new IndexSearcher(reader)

def analyzer = new JapaneseAnalyzer()

def parser = new QueryParser('message', analyzer)

def query = parser.parse(searchWord)

def results = searcher.search(query, 10)

println "hits: ${results.totalHits}"

println ''

results.scoreDocs.each {
	println "score: ${it.score}, doc: ${reader.document(it.doc)}"
}
