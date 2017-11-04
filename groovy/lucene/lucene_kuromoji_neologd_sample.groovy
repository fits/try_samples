@GrabResolver(name = 'codelibs', root = 'http://maven.codelibs.org/')
@Grab('org.codelibs:lucene-analyzers-kuromoji-ipadic-neologd:7.0.0-20171012')
import org.codelibs.neologd.ipadic.lucene.analysis.ja.JapaneseTokenizer
import org.codelibs.neologd.ipadic.lucene.analysis.ja.tokenattributes.*
import org.apache.lucene.analysis.tokenattributes.*

def tokenizer = new JapaneseTokenizer(null, true, JapaneseTokenizer.DEFAULT_MODE)

def bform = tokenizer.addAttribute(BaseFormAttribute)
def pspeech = tokenizer.addAttribute(PartOfSpeechAttribute)
def inf = tokenizer.addAttribute(InflectionAttribute)
def rd = tokenizer.addAttribute(ReadingAttribute)
def term = tokenizer.addAttribute(CharTermAttribute)

new File(args[0]).withReader('UTF-8') {
	tokenizer.reader = it

	tokenizer.reset()

	while (tokenizer.incrementToken()) {
		println "term=${term}, baseForm=${bform.baseForm}, partOfSpeech=${pspeech.partOfSpeech}, type=${inf.inflectionType}, form=${inf.inflectionForm}, reading=${rd.reading}, pronunciation=${rd.pronunciation}"

		//println tokenizer.reflectAsString(false)
	}

	tokenizer.close()
}
