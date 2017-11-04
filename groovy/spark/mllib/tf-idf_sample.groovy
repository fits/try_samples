@Grab('org.apache.spark:spark-mllib_2.11:2.2.0')
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.feature.IDF

def file = args[0]

def conf = new SparkConf().setMaster('local').setAppName('TF-IDFSample')
def ctx = new JavaSparkContext(conf)

def data = ctx.textFile(file)

def words = data.map( { it.trim().split(' ').toList() }.dehydrate() )
words.cache()

def hashTf = new HashingTF()

def tf = hashTf.transform(words).cache()
tf.cache()

println tf.collect()

def idf = new IDF().fit(tf)
def tfidf = idf.transform(tf)

println '----------'

println tfidf.collect()
