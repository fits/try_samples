
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class SampleApp {
	public static void main(String... args) {
		INDArray x = Nd4j.create(new double[][] {
			{1, 2},
			{3, 4}
		});

		INDArray y = Nd4j.create(new double[][] {
			{5, 6},
			{7, 8}
		});

		System.out.println( x.add(y) );

		System.out.println("-----");

		System.out.println( x.mmul(y) );

		System.out.println("-----");

		System.out.println( x.transpose() );
	}
}
