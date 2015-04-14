
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors

object LogiRegApp extends App {
	val factor = (s: String) => s match {
		case "C" => 0.0
		case _ => 1.0
	}

	val sc = new SparkContext("local", "LogiRegSampleApp")

	val rdd = sc.textFile(args(0)).map(_.split(",")).map { d =>
		val v = Vectors.dense(d(2).toDouble, factor(d(3)))
		LabeledPoint(d(1).toDouble, v)
	}

	println(rdd.count)
	println(rdd.first())

	val res = new LogisticRegressionWithLBFGS()
		.setNumClasses(9)
		.setIntercept(true)
		.run(rdd)

	println(res)

	val xlist = rdd.map(_.features.toArray(0))

	val xx = Range.Double.inclusive(xlist.min(), xlist.max(), 0.1)

	xx.foreach { x =>
		val y = res.predict(Vectors.dense(x, 0.0))
		println(s"${x},${y}")
	}
}