@Grab('org.deeplearning4j:deeplearning4j-core:0.4-rc3.8')
@Grab('org.nd4j:nd4j-x86:0.4-rc3.8')
import org.deeplearning4j.datasets.DataSets
import org.deeplearning4j.eval.Evaluation
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.Updater
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.dataset.SplitTestAndTrain
import org.nd4j.linalg.lossfunctions.LossFunctions

def irisTypesNum = 3

def epoch = args[0] as int
def trainRate = 0.7
def batchSize = 1

def learningRate = args[1] as double
def updateMethod = Updater.valueOf(args[2].toUpperCase())

def fcNeuNum = args[3] as int
def fcAct = args[4]

def data = DataSets.iris()

def conf = new NeuralNetConfiguration.Builder()
	.iterations(1)
	.updater(updateMethod)
	.learningRate(learningRate)
	.list(2)
	.layer(0, new DenseLayer.Builder()
		.nIn(4)
		.nOut(fcNeuNum)
		.activation(fcAct)
		.build()
	)
	.layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
		.nIn(fcNeuNum)
		.nOut(irisTypesNum)
		.activation("softmax")
		.build()
	)
	.build()

def calcResult = { model, d ->
	def ev = new Evaluation(irisTypesNum)

	ev.eval(d.labels, model.output(d.featureMatrix))

	[
		loss: model.score(d),
		accuracy: ev.accuracy()
	]
}

def model = new MultiLayerNetwork(conf)
model.init()

(0..<epoch).each {
	data.shuffle()

	def testAndTrain = data.splitTestAndTrain(trainRate)

	testAndTrain.train.batchBy(batchSize).each {
		model.fit(it)
	}

	def res = [
		calcResult(model, testAndTrain.train),
		calcResult(model, testAndTrain.test)
	]

	println([
		res[0].loss, res[0].accuracy,
		res[1].loss, res[1].accuracy
	].join(','))
}
