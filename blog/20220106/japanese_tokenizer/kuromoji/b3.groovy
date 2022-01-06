@Grab('com.atilika.kuromoji:kuromoji-ipadic:0.9.0')
import com.atilika.kuromoji.ipadic.Tokenizer

def text = args[0]
def tokenizer = new Tokenizer()

tokenizer.tokenize(args[0]).each {
    def pos = [
        it.partOfSpeechLevel1,
        it.partOfSpeechLevel2,
        it.partOfSpeechLevel3,
        it.partOfSpeechLevel4
    ]

    println "term=${it.surface}, partOfSpeech=${pos}"
}
