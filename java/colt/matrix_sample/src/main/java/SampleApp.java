
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;

public class SampleApp {
	public static void main(String... args) {
		DoubleMatrix2D x = DoubleFactory2D.dense.make(new double[][] {
			{1, 2},
			{3, 4}
		});

		DoubleMatrix2D y = DoubleFactory2D.dense.make(new double[][] {
			{5, 6},
			{7, 8}
		});

		System.out.println( x.copy().assign(y, Functions.plus) );

		System.out.println("-----");

		Algebra algebra = new Algebra();

		System.out.println( algebra.mult(x, y) );

		System.out.println("-----");

		System.out.println( x.viewDice() );
	}
}
