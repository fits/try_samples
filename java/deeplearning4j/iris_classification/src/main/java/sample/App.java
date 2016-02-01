package sample;

import org.deeplearning4j.datasets.DataSets;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.stream.IntStream;

public class App {
    public static void main(String... args) throws Exception {
        int epoch = 20;
        int pnum = 8;
        double trainRate = 0.7;
        double learningRate = 0.01;

        DataSet data = DataSets.iris();

        MultiLayerNetwork model = createLayerModel(learningRate, pnum);
        model.setListeners(new ScoreIterationListener());

        model.init();

        IntStream.range(0, epoch).forEach(i -> {
            data.shuffle();

            SplitTestAndTrain testAndTrain = data.splitTestAndTrain(trainRate);
            DataSet train = testAndTrain.getTrain();
            DataSet test = testAndTrain.getTest();

            // 学習
            train.forEach(model::fit);

            // 評価
            Evaluation eval = new Evaluation();
            INDArray output = model.output(test.getFeatureMatrix());
            eval.eval(test.getLabels(), output);

            System.out.println(eval.stats());
        });
    }

    private static MultiLayerNetwork createLayerModel(double learningRate, int pnum) throws Exception {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .iterations(1)
                .seed(12345L)
                .learningRate(learningRate)
                .updater(Updater.ADAM)
                .list(2)
                .layer(
                        0,
                        new DenseLayer.Builder()
                                .nIn(4)
                                .nOut(pnum)
                                .activation("relu")
                                .build()
                )
                .layer(
                        1,
                        new OutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                                .nIn(pnum)
                                .nOut(3)
                                .activation("softmax")
                                .build()
                )
                .build();

        return new MultiLayerNetwork(conf);
    }
}
