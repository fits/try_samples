import spark._
import SparkContext._

object GitHubRecommendDistance {
	def main(args: Array[String]) {

		val spark = new SparkContext("local", "GitHubRecommendDistance")

		val file = spark.textFile(args(0))
		val targetUser = args(1)

		val itemsRes = file.map {l =>
			val items = l.split(",")
			(items(3), (items(1), 1.0))
		}.groupByKey().mapValues {v =>
			val target = v.find { case (u, _) => u == targetUser }

			target match {
				case Some((u, p)) => v.map {
					case (tu, tp) => (tu, tp - p)
				}
				case None => v.map { case (u, _) => (u, None) }
			}
		}

		val usersRes = itemsRes.flatMap { case (k, v) =>
			v.map { case (u, p) => (u, (k, p))  }
		}.groupByKey().mapValues {v =>
			val point = v.foldLeft(0.0) {(x, y) =>
				y._2 match {
					case p: Double => x + 1.0 / (1.0 + p)
					case None => x
				}
			}

			(point, v)
		}

		val filteredRes = usersRes.filter {case (u, (p, v)) => p > 7 && u != targetUser }

		val res = filteredRes.flatMap {case (u, (p, v)) =>
			v.filter { case(k, p) => p == None }.map {case(k, p) =>
				(k, 1)
			}
		}.reduceByKey(_ + _)

		res.collect.sortBy { case (k, p) => p }(Ordering.Int.reverse).take(5).foreach {case(k, p) =>
			println(k + " : " + p)
		}
	}
}
