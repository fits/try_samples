
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.optimize.listeners.ScoreIterationListener

import org.nd4j.linalg.api.ndarray.NdArrayJSONWriter

import java.io.File

fun main(args: Array<String>) {

    val confJson = File(args[0]).readText()
    val destFile = args[1]

    val conf = MultiLayerConfiguration.fromJson(confJson)
    val network = MultiLayerNetwork(conf)

    network.init()
    network.setListeners(ScoreIterationListener())

    val trainData = MnistDataSetIterator(100, true, 0)

    network.fit(trainData)

    NdArrayJSONWriter.write(network.params(), destFile)
}