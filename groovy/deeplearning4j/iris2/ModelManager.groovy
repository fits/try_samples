@Grab('org.deeplearning4j:deeplearning4j-core:0.4-rc3.8')
@Grab('org.nd4j:nd4j-x86:0.4-rc3.8')
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork

abstract class ModelManager extends Script {

	def loadModel(String fileName) {
		new File(fileName).withObjectInputStream(this.class.classLoader) {
			it.readObject() as MultiLayerNetwork
		}
	}

	def saveModel(String fileName, MultiLayerConfiguration conf) {
		def model = new MultiLayerNetwork(conf)
		model.init()

		new File(fileName).withObjectOutputStream {
			it.writeObject model
		}
	}
}