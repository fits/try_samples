import spark._
import SparkContext._

object MoneyCount {
	def main(args: Array[String]) {

		val spark = new SparkContext("local", "MoneyCount")

		val file = spark.textFile(args(0))
		val res = file.map {
			(_, 1)
		}.reduceByKey(_ + _)

		res.foreach {t =>
			println(t._1 + " = " + t._2)
		}
	}
}
