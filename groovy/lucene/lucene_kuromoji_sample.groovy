@Grab('org.apache.lucene:lucene-analyzers-kuromoji:5.0.0')
//@Grab('org.apache.lucene:lucene-analyzers-kuromoji:4.10.1')
import org.apache.lucene.analysis.ja.JapaneseTokenizerFactory
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute

def setting = [mode : 'search']

def factory = new JapaneseTokenizerFactory(setting)

// 5.0
def tokenizer = factory.create()
tokenizer.reader = new StringReader(args[0])

// 4.x
//def tokenizer = factory.create(new StringReader(args[0]))

def charAttr = tokenizer.addAttribute(CharTermAttribute)

tokenizer.reset()

while(tokenizer.incrementToken()) {
	println charAttr
}

tokenizer.end()
tokenizer.close()
