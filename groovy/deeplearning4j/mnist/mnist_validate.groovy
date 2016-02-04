@Grab('org.deeplearning4j:deeplearning4j-core:0.4-rc3.8')
@Grab('org.nd4j:nd4j-x86:0.4-rc3.8')
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.eval.Evaluation

import groovy.transform.BaseScript

@BaseScript MnistLoader baseScript

def batchSize = 50

def loadModel = { f -> 
	new File(f).withObjectInputStream(this.class.classLoader) {
		it.readObject() as MultiLayerNetwork
	}
}

def model = loadModel(args[0])

def testData = loadMnist('t10k-images.idx3-ubyte', 't10k-labels.idx1-ubyte')

def ev = new Evaluation(LABELS_NUM)

testData.batchBy(batchSize).each {
	ev.eval(
		it.labels, 
		model.output(it.featureMatrix)
	)

	//println "accuray = ${ev.accuracy()}"
}

println ev.stats()
