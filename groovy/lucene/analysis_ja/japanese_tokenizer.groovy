@Grab('org.apache.lucene:lucene-analyzers-kuromoji:8.11.0')
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer.Mode
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute

def tokenizer = new JapaneseTokenizer(null, true, Mode.SEARCH)

def term = tokenizer.addAttribute(CharTermAttribute)
def pos = tokenizer.addAttribute(PartOfSpeechAttribute)

tokenizer.reader = new StringReader(args[0])
tokenizer.reset()

while(tokenizer.incrementToken()) {
    println "term=${term}, partOfSpeech=${pos.partOfSpeech}"
}

tokenizer.close()
