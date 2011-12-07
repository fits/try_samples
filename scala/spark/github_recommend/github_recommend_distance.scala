import spark._
import SparkContext._

object GitHubRecommendDistance {
	def main(args: Array[String]) {

		val spark = new SparkContext("local", "GitHubRecommendDistance")

		val file = spark.textFile(args(0))
		val targetUser = args(1)

		val res = file.map {l =>
			val items = l.split(",")
			(items(3), (items(1), 1))
		}.groupByKey().mapValues {v =>
			val target = v.find { case (u, _) => u == targetUser }

			target match {
				case Some((u, p)) => v.map {
					case (tu, tp) => (tu, Math.pow((tp - p).toDouble, 2))
				}
				case None => v.map { case (u, _) => (u, None) }
			}
		}

		res.foreach {case (u, v) =>
			println(u + " = " + v)
		}
	}
}
