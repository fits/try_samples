@Grab('org.deeplearning4j:deeplearning4j-core:0.4-rc3.8')
@Grab('org.nd4j:nd4j-x86:0.4-rc3.8')
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.Updater
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.nd4j.linalg.lossfunctions.LossFunctions

import groovy.transform.BaseScript

@BaseScript ModelManager baseScript

def learningRate = args[0] as double
def updateMethod = Updater.valueOf(args[1].toUpperCase())
def destFile = args[2]

def conf = new NeuralNetConfiguration.Builder()
	.iterations(1)
	.updater(updateMethod)
	.learningRate(learningRate)
	.list(1)
	.layer(0, new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
		.nIn(4)
		.nOut(3)
		.activation('softmax')
		.build()
	)
	.build()

saveModel(destFile, conf)
