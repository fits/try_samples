@Grab('org.deeplearning4j:deeplearning4j-core:0.4-rc3.8')
@Grab('org.nd4j:nd4j-x86:0.4-rc3.8')
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.lossfunctions.LossFunctions

import groovy.transform.BaseScript

@BaseScript MnistLoader baseScript

def epoch = args[0] as int
def batchSize = args[1] as int
def nNum = args[2] as int
def activation = args[3] // 'relu' or 'sigmoid'
def destFile = args[4]

def saveModel = { file, model -> 
	new File(file).withObjectOutputStream {
		it.writeObject model
	}
}

def trainData = loadMnist('train-images.idx3-ubyte', 'train-labels.idx1-ubyte')

def builder = new NeuralNetConfiguration.Builder()
	.iterations(1)
	.seed(123)
	.list(2)
	.layer(
		0,
		new DenseLayer.Builder()
			.nIn(28 * 28)
			.nOut(nNum)
			.activation(activation)
			.build()
	)
	.layer(
		1,
		new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
			.nIn(nNum)
			.nOut(10)
			.activation("softmax")
			.build()
	)

def model = new MultiLayerNetwork(builder.build())

model.init()

(0..<epoch).each {
	println "*** epoch: ${it}"

	trainData.shuffle()

	trainData.batchBy(batchSize).each { d ->
		model.fit(d)
	}
}

saveModel(destFile, model)
