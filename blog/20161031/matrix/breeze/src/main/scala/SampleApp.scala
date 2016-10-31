
import breeze.linalg.DenseMatrix

object SampleApp extends App {
	val x = DenseMatrix((1.0, 2.0), (3.0, 4.0))
	val y = DenseMatrix((5.0, 6.0), (7.0, 8.0))

	println( x + y )

	println("-----")

	println( x * y )

	println("-----")

	println( x.t )
}
