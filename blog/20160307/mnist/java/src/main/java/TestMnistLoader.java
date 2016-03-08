
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

import java.util.stream.IntStream;

public class TestMnistLoader {
    public static void main(String... args) {

        MnistLoader.loadMnist(args[0], args[1])
            .thenAccept(ds -> {
                System.out.println("size: " + ds.numExamples());

                printData(ds.get(0));

                System.out.println("----------");

                printData(ds.get(1));
            })
            .join();
    }

    private static void printData(DataSet d) {
        System.out.println("***** labels = " + d.getLabels());

        INDArray v = d.getFeatures();

        IntStream.range(0, 28).forEach( y -> {
            IntStream.range(0, 28).forEach ( x -> {
                System.out.print( v.getInt(x + y * 28) > 0 ? "#" : " " );
            });

            System.out.println();
        });

        System.out.println(d.getFeatures());
    }
}
