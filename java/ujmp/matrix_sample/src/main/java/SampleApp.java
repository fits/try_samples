
import org.ujmp.core.DenseMatrix;
import org.ujmp.core.Matrix;

public class SampleApp {
	public static void main(String... args) {
		Matrix x = DenseMatrix.Factory.linkToArray(
			new double[] {1, 2},
			new double[] {3, 4}
		);

		Matrix y = DenseMatrix.Factory.linkToArray(
			new double[] {5, 6},
			new double[] {7, 8}
		);

		System.out.println( x.plus(y) );

		System.out.println("-----");

		System.out.println( x.mtimes(y) );

		System.out.println("-----");

		System.out.println( x.transpose() );
	}
}
