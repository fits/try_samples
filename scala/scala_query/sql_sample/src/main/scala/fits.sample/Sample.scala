package fits.sample

import org.scalaquery.session._
import org.scalaquery.session.Database.threadLocalSession
import org.scalaquery.simple.StaticQuery
import org.scalaquery.simple.GetResult

case class Station(val prefName: String, val stationGroupCode: Int, val stationName: String, val lines: Int)

object Sample {
	def main(args: Array[String]) = {

		Database.forURL("jdbc:h2:mem:", driver = "org.h2.Driver") withSession {

/*  
		//threadLocalSession を import しない場合は以下のようにする必要あり
		Database.forURL("jdbc:h2:mem:", driver = "org.h2.Driver") withSession {s: Session =>
			implicit val session = s
*/
			StaticQuery.queryNA[String]("SELECT DISTINCT station_name FROM CSVREAD('m_station.csv')") foreach {r =>
				println("" + r)
			}

			println("---------------")

			StaticQuery.query[(Int, String), (String, String)]("SELECT rr_name, station_name FROM CSVREAD('m_station.csv') WHERE pref_cd=? and rr_name=?") foreach ((14, "JR"), r => printf("%s %s駅\n", r._1, r._2) )

			println("---------------")

			val sql = """
				SELECT *
				FROM (
					SELECT
						pref_name,
						station_g_cd,
						station_name,
						count(*) as lines
					FROM
					  CSVREAD('m_station.csv') S
					  JOIN CSVREAD('m_pref.csv') P
					    ON S.pref_cd=P.pref_cd
					GROUP BY station_g_cd, station_name
					ORDER BY lines DESC
				)
				WHERE ROWNUM <= 10
			"""

			//Tuple 版
			StaticQuery.queryNA[(String, Int, String, Int)](sql) foreach {r =>
				printf("%s駅 (%s) : %d\n", r._3, r._1, r._4)
			}

			println("---------------")

			//ケースクラス Station 版
			implicit val getStationResult = GetResult(r => new Station(r <<, r <<, r <<, r <<))
			StaticQuery.queryNA(sql) foreach {r =>
				printf("%s駅 (%s) : %d\n", r.stationName, r.prefName, r.lines)
			}
		}
	}
}
