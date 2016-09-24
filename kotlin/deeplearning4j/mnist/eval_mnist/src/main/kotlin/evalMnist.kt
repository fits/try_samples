
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator
import org.deeplearning4j.nn.conf.MultiLayerConfiguration
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork

import org.nd4j.linalg.api.ndarray.NdArrayJSONReader

import java.io.File

fun main(args: Array<String>) {

    val confJson = File(args[0]).readText()
    val params = NdArrayJSONReader().read(File(args[1]))

    val conf = MultiLayerConfiguration.fromJson(confJson)
    val network = MultiLayerNetwork(conf, params)

    val testData = MnistDataSetIterator(1, false, 0)

    val res = network.evaluate(testData)

    println(res.stats())
}