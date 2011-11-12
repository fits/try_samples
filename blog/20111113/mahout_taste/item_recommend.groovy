@Grab("org.apache.mahout:mahout-core:0.5")
@Grab("org.slf4j:slf4j-jdk14:1.6.3")
//@Grab("org.slf4j:slf4j-nop:1.6.3")
import org.apache.mahout.cf.taste.impl.recommender.*
import org.apache.mahout.cf.taste.impl.similarity.*
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel

if (args.length < 2) {
	println "${new File(System.getProperty('script.name')).name} <csv file> <target userID>"
	return
}

def data = new FileDataModel(new File(args[0]))

//コサイン類似度
//def similarity = new UncenteredCosineSimilarity(data)
//ユークリッド距離
//def similarity = new EuclideanDistanceSimilarity(data)
def similarity = new CityBlockSimilarity(data)

def recommender = new GenericItemBasedRecommender(data, similarity)

recommender.recommend(Long.parseLong(args[1]), 5).each {
	println "result : ${it.itemID}, ${it.value}"
}

