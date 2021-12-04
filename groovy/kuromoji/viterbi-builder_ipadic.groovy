@Grab('com.atilika.kuromoji:kuromoji-ipadic:0.9.0')
import com.atilika.kuromoji.ipadic.Tokenizer
import com.atilika.kuromoji.viterbi.ViterbiBuilder

def builder = new Tokenizer.Builder()
builder.loadDictionaries()

def vtBuilder = new ViterbiBuilder(
    builder.doubleArrayTrie,
    builder.tokenInfoDictionary,
    builder.unknownDictionary,
    builder.userDictionary,
    builder.mode
)

def lattice = vtBuilder.build(args[0])

def printIndexAttr = { attr ->
    attr.each {
        it.each {
            if (it) {
                println "surface=${it.surface}, startIndex=${it.startIndex}, wordId=${it.wordId}, wordCost=${it.wordCost}"
            }
        }
    }
}

printIndexAttr(lattice.startIndexArr)
