
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors

object LogisticRegression extends App {
	val factor = (s: String) => s match {
		case "C" => 0
		case _ => 1
	}

	val sc = new SparkContext("local", "LogisticRegression")

	val rdd = sc.textFile(args(0)).map(_.split(",")).flatMap { d =>
		val n = d(0).toInt
		val x = d(1).toInt

		val v = Vectors.dense(d(2).toDouble, factor(d(3)))

		List.fill(x)( LabeledPoint(1, v) ) ++ 
			List.fill(n -x)( LabeledPoint(0, v) )
	}

//	println(rdd.count)

	val res = new LogisticRegressionWithLBFGS()
//		.setNumClasses(2) //省略可能
		.setIntercept(true)
		.run(rdd)

	println(res)
}