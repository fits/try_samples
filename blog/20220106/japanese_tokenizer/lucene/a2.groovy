@GrabResolver('https://maven.codelibs.org/')
@Grab('org.codelibs:lucene-analyzers-kuromoji-ipadic-neologd:8.2.0-20200120')
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer.Mode
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute

def text = args[0]

new JapaneseTokenizer(null, false, Mode.NORMAL).withCloseable { tokenizer ->
    def term = tokenizer.addAttribute(CharTermAttribute)
    def pos = tokenizer.addAttribute(PartOfSpeechAttribute)

    tokenizer.reader = new StringReader(text)
    tokenizer.reset()

    while(tokenizer.incrementToken()) {
        println "term=${term}, partOfSpeech=${pos.partOfSpeech}"
    }
}
