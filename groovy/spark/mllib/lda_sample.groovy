@Grab('org.apache.spark:spark-mllib_2.11:2.2.0')
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaPairRDD
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.clustering.LDA
import org.apache.spark.mllib.linalg.Vectors

def file = args[0]

def conf = new SparkConf().setMaster('local').setAppName('LDASample')
def ctx = new JavaSparkContext(conf)

def data = ctx.textFile(file)

def words = data.map( { s ->
	double[] d = s.trim().split(' ').collect { it as double }.toArray()

	Vectors.dense(d)

}.dehydrate() )

def corpus = JavaPairRDD.fromJavaRDD(
	words.zipWithIndex().map( { it.swap() }.dehydrate() )
)

corpus.cache()

def model = new LDA().setK(3).run(corpus)

println "vocabSize: ${model.vocabSize()}"

println '----- describeTopics -----'

model.describeTopics().eachWithIndex { v, i ->
	println "--- Topic ${i}:"

	println v._1()
	println v._2()
}

println '----- topicsMatrix -----'

println model.topicsMatrix()

ctx.stop()