@Grab('org.deeplearning4j:deeplearning4j-core:0.4-rc3.8')
@Grab('org.nd4j:nd4j-x86:0.4-rc3.8')
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer
import org.deeplearning4j.nn.conf.layers.setup.ConvolutionLayerSetup
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.lossfunctions.LossFunctions

import groovy.transform.BaseScript

@BaseScript MnistLoader baseScript

def epoch = args[0] as int
def batchSize = args[1] as int
def destFile = args[2]

def saveModel = { file, model -> 
	new File(file).withObjectOutputStream {
		it.writeObject model
	}
}

def trainData = loadMnist('train-images.idx3-ubyte', 'train-labels.idx1-ubyte')

def builder = new NeuralNetConfiguration.Builder()
	.iterations(1)
	.seed(123)
	.list(3)
	.layer(
		0,
		new ConvolutionLayer.Builder(5, 5)
			.nIn(1)
			.nOut(8)
			.padding(2, 2)
			.activation("relu")
			.build()
	)
	.layer(
		1,
		new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX, [2, 2] as int[])
			.stride(2, 2)
			.build()
	)
	.layer(
		2,
		new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
			.nOut(10)
			.activation("softmax")
			.build()
	)

new ConvolutionLayerSetup(builder, 28, 28, 1)

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
