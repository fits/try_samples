package sample;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class App {
    public static void main(String... args) throws Exception {
        DataSet data = readData(args[0]);

        System.out.println(data);

        data.shuffle();

        MultiLayerNetwork model = createLayer();
        model.setListeners(new ScoreIterationListener());

        model.init();

        SplitTestAndTrain testAndTrain = data.splitTestAndTrain(0.8);

        model.fit(testAndTrain.getTrain());

        System.out.println(model.params());

        System.out.println("score : " + model.score(testAndTrain.getTest()));

    }

    private static DataSet readData(String fileName) throws Exception {
        List<DataSet> dataSetList = Files.lines(Paths.get(fileName))
                .map(line -> line.split(","))
                .flatMap(App::toDataSet)
                .collect(Collectors.toList());

        return DataSet.merge(dataSetList);
    }

    private static Stream<DataSet> toDataSet(String[] values) {
        int num = Integer.parseInt(values[0]);
        int aliveNum = Integer.parseInt(values[1]);

        INDArray features = Nd4j.create(new double[]{
                Double.parseDouble(values[2]),
                factor(values[3])
        });

        return IntStream.range(0, num)
                .mapToObj(n ->
                        new DataSet(features, Nd4j.create(new double[]{(n < aliveNum) ? 1.0 : 0.0})));
    }

    private static double factor(String value) {
        return (value.equals("C"))? 0.0: 1.0;
    }

    private static MultiLayerNetwork createLayer() throws Exception {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(1)
                .iterations(1000)
                .optimizationAlgo(OptimizationAlgorithm.LBFGS)
                .list(1)
                .layer(
                        0,
                        new OutputLayer.Builder(LossFunctions.LossFunction.XENT)
                                .nIn(2)
                                .nOut(1)
                                .activation("sigmoid")
                                .weightInit(WeightInit.XAVIER)
                                .build()
                )
                .build();

        return new MultiLayerNetwork(conf);
    }
}
