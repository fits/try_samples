@GrabResolver('https://maven.codelibs.org/')
@Grab('org.codelibs:lucene-analyzers-kuromoji-ipadic-neologd:8.2.0-20200120')
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseAnalyzer
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute

def analyzer = new JapaneseAnalyzer()

def ts = analyzer.tokenStream(null, args[0])

ts.reset()

while(ts.incrementToken()) {
    def term = ts.getAttribute(CharTermAttribute)
    def pos = ts.getAttribute(PartOfSpeechAttribute)

    println "term=${term}, partOfSpeech=${pos.partOfSpeech}"
}

ts.close()
