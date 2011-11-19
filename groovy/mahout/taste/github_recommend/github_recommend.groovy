@Grab("org.apache.mahout:mahout-core:0.5")
@Grab("org.slf4j:slf4j-jdk14:1.6.3")
//@Grab("org.slf4j:slf4j-nop:1.6.3")
import org.apache.mahout.cf.taste.impl.common.FastByIDMap
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender
import org.apache.mahout.cf.taste.impl.recommender.svd.*
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel
import org.apache.mahout.cf.taste.impl.model.BooleanPreference
import org.apache.mahout.cf.taste.impl.similarity.*

if (args.length < 3) {
	println "${new File(System.getProperty('script.name')).name} <data file> <target userName> <recommend type>"
	return
}

def users = [:]

class CustomDataModel extends FileDataModel {
	Map users
	Map items

	CustomDataModel(File dataFile) {
		super(dataFile)
	}

	@Override
	protected void processLine(String line,
                           FastByIDMap<?> data,
                           FastByIDMap<FastByIDMap<Long>> timestamps,
                           boolean fromPriorData) {

		def cols = line.split(",")
		long userId = Long.parseLong(cols[0])
		long itemId = Long.parseLong(cols[2])

		if (users == null) {
			users = [:]
		}
		if (items == null) {
			items = [:]
		}

		users.put(userId, cols[1])
		items.put(itemId, cols[3])

		if (!data.containsKey(userId)) {
			data.put(userId, [])
		}

		data.get(userId).add(new BooleanPreference(userId, itemId))
	}
}

def selectRecommender = {t, d ->
	switch (t) {
		case "2":
			//ユークリッド距離
			new GenericItemBasedRecommender(d, new EuclideanDistanceSimilarity(d))
			break
		case "3":
			//マンハッタン距離
			new GenericItemBasedRecommender(d, new CityBlockSimilarity(d))
			break

		case "4":
			//Tanimoto係数
			new GenericItemBasedRecommender(d, new TanimotoCoefficientSimilarity(d))
			break

		case "5":
			//Slope One
			new SlopeOneRecommender(d)
			break

		case "6":
			//SVD
			def factorizer = new ALSWRFactorizer(d, 10, 0.05, 5)
			new SVDRecommender(d, factorizer)
			break

		default:
			//コサイン類似度
			new GenericItemBasedRecommender(d, new UncenteredCosineSimilarity(d))
	}
}

def data = new CustomDataModel(new File(args[0]))

def userId = data.users.find { it.value == args[1] }.key

def rec = selectRecommender(args[2], data)

println rec

rec.recommend(userId, 5).each {
	println "recommend : ${it.itemID}, ${data.items[it.itemID]}"
}

