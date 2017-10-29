
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer
import org.deeplearning4j.models.word2vec.Word2Vec
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator
import org.deeplearning4j.text.tokenization.tokenizerfactory.JapaneseTokenizerFactory
import java.io.File

fun main(args: Array<String>) {
    val iter = CollectionSentenceIterator(toLines(File(args[0])))
    val tokenizerFactory = JapaneseTokenizerFactory()

    val vec = Word2Vec.Builder()
            .batchSize(1000)
            .minWordFrequency(5)
            .layerSize(30)
            .iterations(10)
            .iterate(iter)
            //.negativeSample(10.0)
            .tokenizerFactory(tokenizerFactory)
            .build()

    vec.fit()

    //WordVectorSerializer.writeWord2VecModel(vec, "words.txt")

    vec.vocab().tokens().forEach(::println)

    println("""similarity = ${vec.similarity("java", "ニューラルネット")}""")
    vec.wordsNearest(listOf("java", "ニューラルネット"), listOf(), 10).forEach(::println)
}

fun toLines(dir: File): Collection<String> = dir.listFiles().flatMap { it.readLines(Charsets.UTF_8) }.filter { it.isNotBlank() }

