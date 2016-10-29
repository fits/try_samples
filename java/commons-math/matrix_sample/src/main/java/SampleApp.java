
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class SampleApp {
	public static void main(String... args) {
		RealMatrix x = MatrixUtils.createRealMatrix(new double[][] {
			{1, 2},
			{3, 4}
		});

		RealMatrix y = MatrixUtils.createRealMatrix(new double[][] {
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
