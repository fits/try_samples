@Grab('com.atilika.kuromoji:kuromoji-ipadic:0.9.0')
import com.atilika.kuromoji.ipadic.Token
import com.atilika.kuromoji.ipadic.Tokenizer

def tokenizer = new Tokenizer()

tokenizer.tokenize(new File(args[0]).getText('UTF-8')).each {
	println "${it.surface}, ${it.baseForm}, ${it.partOfSpeechLevel1}, ${it.partOfSpeechLevel2}"
}
