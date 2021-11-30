@Grab('com.worksap.nlp:sudachi:0.5.3')
import com.worksap.nlp.sudachi.*

def dic = new DictionaryFactory().create(
    null, 
    '{ "systemDict": "system_small.dic" }', 
    true
)

def tokenizer = dic.create()

def tokens = tokenizer.tokenize(Tokenizer.SplitMode.A, args[0])

tokens.each {
    def pos = dic.getPartOfSpeechString(it.partOfSpeechId())

    println "${it.surface()}, ${pos}"
}

dic.close()
