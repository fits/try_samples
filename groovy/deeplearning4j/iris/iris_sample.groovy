@Grab('org.deeplearning4j:deeplearning4j-core:0.4-rc3.8')
@Grab('org.nd4j:nd4j-x86:0.4-rc3.8')
import org.deeplearning4j.datasets.DataSets
import org.deeplearning4j.eval.Evaluation
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.linalg.dataset.SplitTestAndTrain
import org.nd4j.linalg.lossfunctions.LossFunctions

def epoch = args[0] as int
def learnRate = args[1] as float
def nNum = args[2] as int

def data = DataSets.iris()

def conf = new NeuralNetConfiguration.Builder()
	.iterations(1)
	.seed(123)
//	.updater(Updater.ADAM)
	.list(2)
	.layer(0, new DenseLayer.Builder()
		.nIn(4)
		.nOut(nNum)
		.activation("relu")
		.build()
	)
	.layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
		.nIn(nNum)
		.nOut(3)
		.activation("softmax")
		.build()
	)
	.build()

def model = new MultiLayerNetwork(conf)
model.setListeners(new ScoreIterationListener())

model.init()

def totalEv = new Evaluation(3)

(0..<epoch).each {
	data.shuffle()

	def testAndTrain = data.splitTestAndTrain(learnRate)

	testAndTrain.train.each {
		model.fit(it)
	}

	def test = testAndTrain.test

	def ev = new Evaluation(3)

	ev.eval(test.labels, model.output(test.featureMatrix))
	totalEv.eval(test.labels, model.output(test.featureMatrix))

	println ev.stats()
}

println '***** total *****'

println totalEv.stats()
