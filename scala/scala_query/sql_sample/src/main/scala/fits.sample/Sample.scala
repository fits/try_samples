package fits.sample

import org.scalaquery.session._
import org.scalaquery.simple.StaticQuery

object Sample {
	def main(args: Array[String]) = {

		Database.forURL("jdbc:h2:mem:", driver = "org.h2.Driver") withSession {s: Session =>
			implicit val session = s

			StaticQuery.queryNA[String]("SELECT DISTINCT station_name from CSVREAD('m_station.csv')") foreach {r =>
				println("" + r)
			}

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

			StaticQuery.queryNA[(String, Int, String, Int)](sql) foreach {r =>
				printf("%s駅 (%s) : %d\n", r._3, r._1, r._4)
			}
		}
	}
}
