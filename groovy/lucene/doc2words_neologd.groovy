@GrabResolver(name = 'codelibs', root = 'http://maven.codelibs.org/')
@Grab('org.codelibs:lucene-analyzers-kuromoji-ipadic-neologd:7.0.0-20171012')
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.*
import org.apache.lucene.analysis.tokenattributes.*
import java.text.Normalizer

def target = [
	'名詞-一般',
	'名詞-副詞可能',
	'名詞-サ変接続',
	'名詞-形容動詞語幹',
	'動詞-自立',
	'形容詞-自立'
]

def stopWords = 'ある する なる できる ない いう'.split(' ')

def tokenizer = new JapaneseTokenizer(null, true, JapaneseTokenizer.DEFAULT_MODE)

def term = tokenizer.addAttribute(CharTermAttribute)
def bform = tokenizer.addAttribute(BaseFormAttribute)
def pspeech = tokenizer.addAttribute(PartOfSpeechAttribute)

def doc = Normalizer.normalize(
	new File(args[0]).getText('UTF-8'),
	Normalizer.Form.NFKC
)

tokenizer.reader = new StringReader(doc)

tokenizer.reset()

def words = []

while (tokenizer.incrementToken()) {
	def w = (bform.baseForm == null) ? term.toString() : bform.baseForm

	if (target.contains(pspeech.partOfSpeech) && !stopWords.contains(w)) {
		words << w
	}
}

tokenizer.close()

println words.join(' ')
