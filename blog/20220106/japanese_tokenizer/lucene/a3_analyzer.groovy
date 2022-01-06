@Grab('org.apache.lucene:lucene-analyzers-kuromoji:8.11.1')
import org.apache.lucene.analysis.ja.JapaneseAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute

def text = args[0]

def analyzer = new JapaneseAnalyzer()

analyzer.tokenStream(null, text).withCloseable { ts ->
    ts.reset()

    while(ts.incrementToken()) {
        def term = ts.getAttribute(CharTermAttribute)
        def pos = ts.getAttribute(PartOfSpeechAttribute)

        println "term=${term}, partOfSpeech=${pos.partOfSpeech}"
    }
}
