@GrabResolver('http://www.atilika.org/nexus/content/repositories/atilika')
@Grab('org.atilika.kuromoji:kuromoji:0.7.7')
import org.atilika.kuromoji.Tokenizer

def tokenizer = Tokenizer.builder().build()

tokenizer.tokenize(args[0]).each {
	println "${it.surfaceForm} : ${it.allFeatures}"
}
