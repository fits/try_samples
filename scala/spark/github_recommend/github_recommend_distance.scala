
import scala.math._
import spark._
import SparkContext._


object GitHubRecommendDistance {
	def main(args: Array[String]) {

		val spark = new SparkContext(args(0), "GitHubRecommendDistance")

		val file = spark.textFile(args(1))
		val targetUser = args(2)

		val itemsRes = file.map { l =>
			val items = l.split(",")
			(items(3), (items(1), 1.0))

		}.groupByKey().mapValues { users =>
			val target = users.find { case (user, _) => user == targetUser }

			target match {
				case Some((_, targetPoint)) => users.map { case (user, point) =>
					(user, abs(point - targetPoint))
				}
				case None => users.map { case (user, point) => (user, None) }
			}
		}

		val usersRes = itemsRes.flatMap { case (item, users) =>
			users.map { case (user, point) => (user, (item, point)) }

		}.groupByKey().mapValues { items =>
			val total = items.foldLeft(0.0) { (subTotal, itemTuple) =>
				itemTuple._2 match {
					case p: Double => subTotal + 1.0 / (1.0 + p)
					case None => subTotal
				}
			}

			(total, items)
		}

		val pickupRes = usersRes.filter { case (user, (total, _)) =>
			total > 7 && user != targetUser
		}

		val res = pickupRes.flatMap { case (user, (total, items)) =>
			items.filter { case (_, point) =>
				point == None
			}.map { case (item, _) =>
				(item, 1)
			}
		}.reduceByKey(_ + _)

		implicit val order = Ordering.Int.reverse

		res.collect.sortBy(_._2).take(5).foreach { case (item, point) =>
			printf("%s : %d\n", item, point)
		}
	}
}
