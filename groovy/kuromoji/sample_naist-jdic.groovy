@Grab('com.atilika.kuromoji:kuromoji-naist-jdic:0.9.0')
import com.atilika.kuromoji.naist.jdic.Tokenizer
import com.atilika.kuromoji.naist.jdic.Token

def tokenizer = new Tokenizer()

tokenizer.tokenize(args[0]).each {
    println "${it.surface}, ${it.allFeatures}"
}
