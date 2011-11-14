@Grab("org.apache.mahout:mahout-core:0.5")
@Grab("org.slf4j:slf4j-jdk14:1.6.3")
//@Grab("org.slf4j:slf4j-nop:1.6.3")
import org.apache.mahout.cf.taste.impl.recommender.svd.*
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel

if (args.length < 2) {
	println "${new File(System.getProperty('script.name')).name} <csv file> <target userID>"
	return
}

def data = new FileDataModel(new File(args[0]))

//ƒOƒ‹[ƒv” 10 ‚Å lambda=0.05 ‚Å 5‰ñŒJ‚è•Ô‚·
def factorizer = new ALSWRFactorizer(data, 10, 0.05, 5)

def recommender = new SVDRecommender(data, factorizer)

recommender.recommend(Long.parseLong(args[1]), 5).each {
	println "result : ${it.itemID}, ${it.value}"
}

