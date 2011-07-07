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

		}

	}
}
