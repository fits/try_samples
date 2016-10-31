
import org.la4j.Matrix;

public class SampleApp {
	public static void main(String... args) {
		Matrix x = Matrix.from2DArray(new double[][] {
			{1, 2},
			{3, 4}
		});

		Matrix y = Matrix.from2DArray(new double[][] {
			{5, 6},
			{7, 8}
		});

		System.out.println( x.add(y) );

		System.out.println("-----");

		System.out.println( x.multiply(y) );

		System.out.println("-----");

		System.out.println( x.transpose() );
	}
}
