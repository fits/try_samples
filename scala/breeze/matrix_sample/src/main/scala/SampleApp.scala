
import breeze.linalg.DenseMatrix

object SampleApp extends App {
	val x = DenseMatrix((1, 2), (3, 4))
	val y = DenseMatrix((5, 6), (7, 8))

	println( x + y )

	println("-----")

	println( x * y )

	println("-----")

	println( x.t )
}
