@Grab("org.apache.mahout:mahout-core:0.5")
@Grab("org.slf4j:slf4j-jdk14:1.6.3")
//@Grab("org.slf4j:slf4j-nop:1.6.3")
import org.apache.mahout.cf.taste.impl.recommender.*
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel

def data = new FileDataModel(new File(args[0]))

def recommender = new ItemUserAverageRecommender(data)

recommender.recommend(1, 5).each {
	println "result : ${it.itemID}, ${it.value}"
}

