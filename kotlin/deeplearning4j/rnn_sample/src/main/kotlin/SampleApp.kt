import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader
import org.datavec.api.split.NumberedFileInputSplit
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.Updater
import org.deeplearning4j.nn.conf.layers.GravesLSTM
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler
import org.nd4j.linalg.lossfunctions.LossFunctions

fun main(args: Array<String>) {
    val miniBatchSize = 20
    val iterations = 300

    val trainReader = CSVSequenceRecordReader()
    trainReader.initialize(NumberedFileInputSplit("data/train_%d.csv", 0, 0))

    val trainData = SequenceRecordReaderDataSetIterator(trainReader, miniBatchSize, -1, 1, true).next()

    val normalizer = NormalizerMinMaxScaler()
    normalizer.fitLabel(true)
    normalizer.fit(trainData)

    normalizer.preProcess(trainData)

    val conf = NeuralNetConfiguration.Builder()
            .iterations(iterations)
            .weightInit(WeightInit.XAVIER)
            .updater(Updater.ADAM).adamMeanDecay(0.9).adamVarDecay(0.999)
            .learningRate(0.001)
            .list(
                    GravesLSTM.Builder()
                            .activation(Activation.TANH)
                            .nIn(1)
                            .nOut(10)
                            .build(),
                    RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE)
                            .activation(Activation.IDENTITY)
                            .nOut(1)
                            .build()
            )
            .build()

    val net = MultiLayerNetwork(conf)
    net.init()

    net.setListeners(ScoreIterationListener(20))

    net.fit(trainData)
}