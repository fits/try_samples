package fits.sample

import org.scalaquery.session._
import org.scalaquery.simple.StaticQuery
import org.scalaquery.simple.GetResult

object Sample {
	def main(args: Array[String]) = {

		Database.forURL("jdbc:h2:mem:", driver = "org.h2.Driver") withSession {s: Session =>
			implicit val session = s

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
			case class Station(val prefName: String, val stationGroupCode: Int, val stationName: String, val lines: Int)

			implicit val getStationResult = GetResult(r => new Station(r <<, r <<, r <<, r <<))
			StaticQuery.queryNA(sql) foreach {r =>
				printf("%s駅 (%s) : %d\n", r.stationName, r.prefName, r.lines)
			}

			println("---------------")

			val sql2 = """
				SELECT
					rr_name,
					station_name
				FROM
					CSVREAD('m_station.csv')
				WHERE
					pref_cd = ? AND
					rr_name = ?
			"""

			StaticQuery.query[(Int, String), (String, String)](sql2) foreach (
				(14, "JR"), r => printf("%s %s駅\n", r._1, r._2)
			)

		}
	}
}
