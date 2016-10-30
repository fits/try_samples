
import org.jblas.DoubleMatrix;

public class SampleApp {
	public static void main(String... args) {

		DoubleMatrix x = new DoubleMatrix(new double[][] {
			{1, 2},
			{3, 4}
		});

		DoubleMatrix y = new DoubleMatrix(new double[][] {
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
