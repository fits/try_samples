@Grab('org.apache.lucene:lucene-analyzers-kuromoji:8.11.1')
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer.Mode
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute

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
