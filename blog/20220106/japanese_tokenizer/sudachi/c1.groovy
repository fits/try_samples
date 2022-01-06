@Grab('com.worksap.nlp:sudachi:0.5.3')
import com.worksap.nlp.sudachi.DictionaryFactory
import com.worksap.nlp.sudachi.Tokenizer

def text = args[0]

new DictionaryFactory().create().withCloseable { dic ->
    def tokenizer = dic.create()
    def ts = tokenizer.tokenize(Tokenizer.SplitMode.C, text)

    ts.each { t ->
        def pos = dic.getPartOfSpeechString(t.partOfSpeechId())

        println "term=${t.surface()}, partOfSpeech=${pos}"
    }
}
