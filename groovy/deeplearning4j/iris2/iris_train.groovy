@Grab('org.deeplearning4j:deeplearning4j-core:0.4-rc3.8')
@Grab('org.nd4j:nd4j-x86:0.4-rc3.8')
import org.deeplearning4j.datasets.DataSets
import org.deeplearning4j.eval.Evaluation
import org.nd4j.linalg.dataset.SplitTestAndTrain

import groovy.transform.BaseScript

@BaseScript ModelManager baseScript

def irisTypesNum = 3

def epoch = args[0] as int
def trainRate = 0.7
def batchSize = 1

def modelFile = args[1]

def evaluate = { model, d ->
	def ev = new Evaluation(irisTypesNum)

	ev.eval(d.labels, model.output(d.featureMatrix, false))

	[
		loss: model.score(d),
		accuracy: ev.accuracy()
	]
}

def model = loadModel(modelFile)

def data = DataSets.iris()

(0..<epoch).each {
	data.shuffle()

	def testAndTrain = data.splitTestAndTrain(trainRate)

	testAndTrain.train.batchBy(batchSize).each {
		model.fit(it)
	}

	def res = [
		evaluate(model, testAndTrain.train),
		evaluate(model, testAndTrain.test)
	]

	println([
		res[0].loss, res[0].accuracy,
		res[1].loss, res[1].accuracy
	].join(','))
}
