@Grab('org.apache.spark:spark-mllib_2.11:2.2.0')
import org.apache.spark.mllib.feature.HashingTF

def words = new File(args[0]).getText('UTF-8').split(' ').toList()

println words

def hashTf = new HashingTF()

def tf = hashTf.transform(words)

//println tf

[tf.indices(), tf.values()].transpose().each {
	println "${it.first()} : ${it.last()}"
}
