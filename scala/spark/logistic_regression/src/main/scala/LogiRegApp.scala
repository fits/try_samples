
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors

object LogiRegApp extends App {
	val factor = (s: String) => s match {
		case "C" => 0.0
		case _ => 1.0
	}

	val checkAlive = (alive: Int) => (n: Int) => if (n <= alive) 1.0 else 0.0

	val sc = new SparkContext("local", "LogiRegSampleApp")

	val rdd = sc.textFile(args(0)).map(_.split(",")).flatMap { d =>
		val v = Vectors.dense(d(2).toDouble, factor(d(3)))
		val isAlive = checkAlive(d(1).toInt)

		Range.inclusive(1, d(0).toInt).map { n =>
			LabeledPoint(isAlive(n), v)
		}
	}

	println(rdd.count)

	val res = new LogisticRegressionWithLBFGS()
//		.setNumClasses(2) //省略可能
		.setIntercept(true)
		.run(rdd)

	println(res)
}