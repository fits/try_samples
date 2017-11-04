@Grab('org.apache.spark:spark-mllib_2.11:2.2.0')
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaPairRDD
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.clustering.LDA

def file = args[0]

def conf = new SparkConf().setMaster('local').setAppName('LDASample')
def ctx = new JavaSparkContext(conf)

def data = ctx.textFile(file)
def docs = data.map( { it.trim().split(' ').toList() }.dehydrate() )
docs.cache()

def uniqWords = docs.flatMap( { it.iterator() }.dehydrate() ).distinct()
uniqWords.cache()

println uniqWords.count()
println uniqWords.collect()

def hashTf = new HashingTF()

def tf = hashTf.transform(docs)
tf.cache()

def mapping = uniqWords.collect().collectEntries { [hashTf.indexOf(it), it] }

def corpus = JavaPairRDD.fromJavaRDD(
	tf.zipWithIndex().map({ it.swap() }.dehydrate())
)
corpus.cache()

println corpus.collect()

def model = new LDA().setK(3).run(corpus)

println "vocabSize: ${model.vocabSize()}"

println mapping

println '----- describeTopics -----'

model.describeTopics().eachWithIndex { v, i ->

	println "--- Topic ${i}:"

	println(
		[v._1(), v._2()].transpose().collect { t ->
			"${t.first()} - ${mapping[t.first()]} : ${t.last()}"
		}
	)

	println ''
}

ctx.stop()