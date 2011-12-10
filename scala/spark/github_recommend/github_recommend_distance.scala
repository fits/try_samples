
import scala.math._
import spark._
import SparkContext._


object GitHubRecommendDistance {
	def main(args: Array[String]) {

		val spark = new SparkContext(args(0), "GitHubRecommendDistance")

		val file = spark.textFile(args(1))
		val targetUser = args(2)

		//アイテム単位の集計
		val itemsRes = file.map { l =>
			val items = l.split(",")
			(items(3), (items(1), 1.0))

		}.groupByKey().mapValues { users =>
			users.find(_._1 == targetUser) match {
				//ターゲットユーザーを含む場合はポイントの差を設定
				case Some((_, targetPoint)) => users.map { case (user, point) =>
					(user, abs(point - targetPoint))
				}
				//ターゲットユーザーを含まない場合は None を設定
				case None => users.map { case (user, point) => (user, None) }
			}
		}

		//ユーザー単位の集計
		val usersRes = itemsRes.flatMap { case (item, users) =>
			users.map { case (user, point) => (user, (item, point)) }

		}.groupByKey().mapValues { items =>
			//ポイントの合計値を算出
			val total = items.foldLeft(0.0) { (subTotal, itemTuple) =>
				itemTuple._2 match {
					case p: Double => subTotal + 1.0 / (1.0 + p)
					case None => subTotal
				}
			}

			(total, items)
		}

		//ターゲットユーザー以外で
		//ポイントの合計値が 7 より大きいユーザーを抽出
		val pickupRes = usersRes.filter { case (user, (total, _)) =>
			total > 7 && user != targetUser
		}

		//抽出されたユーザーの中でターゲットユーザーが関連していない
		//アイテムをカウント
		val res = pickupRes.flatMap { case (user, (total, items)) =>
			items.filter(_._2 == None).map { case (item, _) =>
				(item, 1)
			}
		}.reduceByKey(_ + _)

/*
		//以下のようにユーザー抽出とアイテムのカウントをまとめて処理する事も可
		val res = usersRes.flatMap {
			case (user, (total, items)) if total > 7 && user != targetUser => {
				items.flatMap {
					case (item, None) => List((item, 1))
					case _ => Nil
				}
			}
			case _ => Nil
		}.reduceByKey(_ + _)
*/

		//sortBy のソート順を設定
		implicit val order = Ordering.Int.reverse
		//カウント数の多い上位 5件のアイテムを取り出して出力
		res.collect.sortBy(_._2).take(5).foreach { case (item, point) =>
			printf("%s : %d\n", item, point)
		}
	}
}
