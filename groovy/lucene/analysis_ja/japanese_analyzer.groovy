@Grab('org.apache.lucene:lucene-analyzers-kuromoji:8.11.0')
import org.apache.lucene.analysis.ja.JapaneseAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute

def analyzer = new JapaneseAnalyzer()

def ts = analyzer.tokenStream(null, args[0])

ts.reset()

while(ts.incrementToken()) {
    def term = ts.getAttribute(CharTermAttribute)
    def pos = ts.getAttribute(PartOfSpeechAttribute)

    println "term=${term}, partOfSpeech=${pos.partOfSpeech}"
}

ts.close()
