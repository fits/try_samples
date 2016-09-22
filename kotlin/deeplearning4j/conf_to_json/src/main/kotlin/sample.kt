import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.inputs.InputType
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer
import org.nd4j.linalg.lossfunctions.LossFunctions

fun main(args: Array<String>) {
    val builder = NeuralNetConfiguration.Builder()
            .iterations(1)
            .list(
                ConvolutionLayer.Builder(5, 5)
                        .nIn(1)
                        .nOut(8)
                        .padding(2, 2)
                        .activation("relu")
                        .build()
                ,
                SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX, intArrayOf(2, 2))
                        .stride(2, 2)
                        .build()
                ,
                OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .nOut(10)
                        .activation("softmax")
                        .build()

            )
            .setInputType(InputType.convolutional(28, 28, 1))

    val conf = builder.build()

    println(conf.toJson())

}