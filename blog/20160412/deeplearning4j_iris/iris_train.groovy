@Grab('org.deeplearning4j:deeplearning4j-core:0.4-rc3.8')
@Grab('org.nd4j:nd4j-x86:0.4-rc3.8')
import org.deeplearning4j.datasets.DataSets
import org.deeplearning4j.eval.Evaluation
import org.nd4j.linalg.dataset.SplitTestAndTrain

import groovy.transform.BaseScript

@BaseScript ModelManager baseScript

def epoch = args[0] as int
def trainRate = 0.7
def batchSize = 1

def modelFile = args[1]

class SimpleEvaluator {
	private def model
	private def ev = new Evaluation(3)
	private def lossList = []

	SimpleEvaluator(model) {
		this.model = model
	}

	def eval(d) {
		lossList << model.score(d)
		ev.eval(d.labels, model.output(d.featureMatrix, false))
	}

	def loss() {
		lossList.sum() / lossList.size()
	}

	def accuracy() {
		ev.accuracy()
	}
}

def model = loadModel(modelFile)

def data = DataSets.iris()

(0..<epoch).each {
	def ev = [
		train: new SimpleEvaluator(model),
		test: new SimpleEvaluator(model)
	]

	data.shuffle()

	def testAndTrain = data.splitTestAndTrain(trainRate)

	testAndTrain.train.batchBy(batchSize).each {
		ev.train.eval(it)

		model.fit(it)
	}

	ev.test.eval(testAndTrain.test)

	println([
		ev.train.loss(), ev.train.accuracy(),
		ev.test.loss(), ev.test.accuracy()
	].join(','))
}
