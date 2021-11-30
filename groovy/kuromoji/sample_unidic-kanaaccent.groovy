@Grab('com.atilika.kuromoji:kuromoji-unidic-kanaaccent:0.9.0')
import com.atilika.kuromoji.unidic.kanaaccent.Tokenizer
import com.atilika.kuromoji.unidic.kanaaccent.Token

def tokenizer = new Tokenizer()

tokenizer.tokenize(args[0]).each {
    println "${it.surface}, ${it.allFeatures}"
}
