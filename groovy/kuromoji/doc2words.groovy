@Grab('com.atilika.kuromoji:kuromoji-ipadic:0.9.0')
import com.atilika.kuromoji.ipadic.Token
import com.atilika.kuromoji.ipadic.Tokenizer
import java.text.Normalizer
import groovy.transform.Immutable

@Immutable
class SpeechLevel {
	String level1
	String level2
}

def target = [
	new SpeechLevel('名詞', '一般'),
	new SpeechLevel('名詞', '固有名詞'),
	new SpeechLevel('名詞', '副詞可能'),
	new SpeechLevel('名詞', 'サ変接続'),
	new SpeechLevel('名詞', '形容動詞語幹'),
	new SpeechLevel('動詞', '自立'),
	new SpeechLevel('形容詞', '自立')
]

def stopWords = '* ある いる する なる できる こと もの'.split(' ')

def tokenizer = new Tokenizer()

def doc = Normalizer.normalize(
	new File(args[0]).getText('UTF-8'),
	Normalizer.Form.NFKC
)

def res = tokenizer.tokenize(doc).findAll {
	target.contains(
		new SpeechLevel(it.partOfSpeechLevel1, it.partOfSpeechLevel2)
	)
}.findAll {
	!stopWords.contains(it.baseForm)
}.collect {
	it.baseForm
}.join(' ')

println res
