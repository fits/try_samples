@Grab('com.atilika.kuromoji:kuromoji-jumandic:0.9.0')
import com.atilika.kuromoji.jumandic.Tokenizer
import com.atilika.kuromoji.jumandic.Token

def tokenizer = new Tokenizer()

tokenizer.tokenize(args[0]).each {
    println "${it.surface}, ${it.allFeatures}"
}
