
import scala.math._
import spark._
import SparkContext._

object SimpleGitHubRecommend {
	def main(args: Array[String]) {
		if (args.length < 3) {
			println("parameters: <host> <data file> <target user>")
			return
		}

		val spark = new SparkContext(args(0), "SimpleGitHubRecommend")

		val file = spark.textFile(args(1))
		val targetUser = args(2)

		// 1. リポジトリ毎に集計
		val itemsRes = file.map { l =>
			val fields = l.split(",")
			//評価値は 1.0 を設定
			(fields(3), (fields(1), 1.0))

		}.groupByKey().mapValues { users =>
			users.find(_._1 == targetUser) match {
				//ターゲットユーザーを含む場合は評価値の差を設定
				case Some((_, targetPoint)) => users.map { case (user, point) =>
					(user, abs(point - targetPoint))
				}
				//ターゲットユーザーを含まない場合は None を設定
				case None => users.map { case (user, point) => (user, None) }
			}
		}

		// 2. ユーザー単位の集計
		val usersRes = itemsRes.flatMap { case (item, users) =>
			users.map { case (user, point) => (user, (item, point)) }

		}.groupByKey().mapValues { items =>
			//評価値の差を使ってスコア算出
			val score = items.foldLeft(0.0) { (subTotal, itemTuple) =>
				itemTuple._2 match {
					case p: Double => subTotal + 1.0 / (1.0 + p)
					case None => subTotal
				}
			}

			(score, items)
		}

		// 3. ターゲットユーザー以外でスコアが 7 より大きいユーザーを抽出
		val pickupRes = usersRes.filter { case (user, (score, _)) =>
			score > 7 && user != targetUser
		}

		// 4. 抽出されたユーザーの中でターゲットユーザーが watch していない
		//リポジトリをカウント
		val res = pickupRes.flatMap { case (user, (score, items)) =>
			items.filter(_._2 == None).map { case (item, _) =>
				(item, 1)
			}
		}.reduceByKey(_ + _)

		//sortBy のソート順を設定
		implicit val order = Ordering.Int.reverse
		// 5. watch しているユーザー数の多い上位 5件のリポジトリを抽出して出力
		res.collect.sortBy(_._2).take(5).foreach { case (item, num) =>
			printf("%s : %d\n", item, num)
		}
	}
}
