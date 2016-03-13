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

import groovy.transform.BaseScript

@BaseScript MnistLoader baseScript

def iterNum = args[0] as int
def batchSize = args[1] as int
def nNum = args[2] as int
def destFile = args[3]

def saveModel = { f, model -> 
	new File(f).withObjectOutputStream {
		it.writeObject model
	}
}

def trainData = loadMnist('train-images.idx3-ubyte', 'train-labels.idx1-ubyte')

def conf = new NeuralNetConfiguration.Builder()
	.iterations(iterNum)
	.seed(123)
//	.learningRate(1e-6f)
	.learningRate(0.001)
//	.useDropConnect(true)
//	.l1(1e-1).regularization(true).l2(2e-4)
	.list(2)
	.layer(0, new RBM.Builder(RBM.HiddenUnit.RECTIFIED, RBM.VisibleUnit.GAUSSIAN)
		.nIn(784)
		.nOut(nNum)
		.activation("relu")
		.lossFunction(LossFunctions.LossFunction.RMSE_XENT)
		.updater(Updater.ADAM)
	//	.dropOut(0.7)
		.build()
	)
	.layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
		.nIn(nNum)
		.nOut(10)
		.activation("softmax")
		.build()
	)
	.build()

def model = new MultiLayerNetwork(conf)
model.setListeners(new ScoreIterationListener())

model.init()

Runtime.addShutdownHook {
	saveModel(destFile, model)
}

def total = trainData.numExamples() / batchSize
def counter = 1

trainData.batchBy(batchSize).each {
	println "*** ${counter++} / ${total}"
	model.fit(it)
}
