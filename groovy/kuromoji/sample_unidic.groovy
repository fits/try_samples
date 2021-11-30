@Grab('com.atilika.kuromoji:kuromoji-unidic:0.9.0')
import com.atilika.kuromoji.unidic.Tokenizer
import com.atilika.kuromoji.unidic.Token

def tokenizer = new Tokenizer()

tokenizer.tokenize(args[0]).each {
    println "${it.surface}, ${it.allFeatures}"
}
