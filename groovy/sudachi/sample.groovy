@Grab('com.worksap.nlp:sudachi:0.5.3')
import com.worksap.nlp.sudachi.*

def dic = new DictionaryFactory().create()
def tokenizer = dic.create()

def tokens = tokenizer.tokenize(Tokenizer.SplitMode.C, args[0])

tokens.each {
    def pos = dic.getPartOfSpeechString(it.partOfSpeechId())

    println "${it.surface()}, ${pos}"
}

dic.close()
