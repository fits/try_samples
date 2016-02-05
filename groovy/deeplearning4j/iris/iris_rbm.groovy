@Grab('org.deeplearning4j:deeplearning4j-core:0.4-rc3.8')
@Grab('org.nd4j:nd4j-x86:0.4-rc3.8')
import org.deeplearning4j.datasets.DataSets
import org.deeplearning4j.eval.Evaluation
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.Updater
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.conf.layers.RBM
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.linalg.dataset.SplitTestAndTrain
import org.nd4j.linalg.lossfunctions.LossFunctions

def learnRate = 0.8

def data = DataSets.iris()

def conf = new NeuralNetConfiguration.Builder()
	.iterations(1000)
	.seed(123)
//	.learningRate(1e-6f)
	//.useDropConnect(true)
	.list(2)
	.layer(0, new RBM.Builder(RBM.HiddenUnit.RECTIFIED, RBM.VisibleUnit.GAUSSIAN)
		.nIn(4)
		.nOut(3)
		.activation("relu")
		.lossFunction(LossFunctions.LossFunction.RMSE_XENT)
		.updater(Updater.ADAM)
	//	.dropOut(0.75)
		.build()
	)
	.layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
		.nIn(3)
		.nOut(3)
		.activation("softmax")
		.build()
	)
	.build()

def model = new MultiLayerNetwork(conf)
model.setListeners(new ScoreIterationListener())

model.init()

def total = new Evaluation()

data.shuffle()
data.normalizeZeroMeanZeroUnitVariance()

def testAndTrain = data.splitTestAndTrain(learnRate)

model.fit(testAndTrain.train)

def ev = new Evaluation()

def test = testAndTrain.test
ev.eval(test.labels, model.output(test.featureMatrix))

println ev.stats()
