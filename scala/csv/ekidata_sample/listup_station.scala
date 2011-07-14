import scala.io.Source

case class Station(val stationName: String, val prefName: Option[String], val stationGroupCode: String)

val prefMap = Source.fromFile("m_pref.csv").getLines().drop(1).map {l =>
	val items = l.split(",")
	items(0) -> items(1)
}.toMap

val lines = Source.fromFile("m_station.csv").getLines()

val list = lines.drop(1).map(_.split(",")).toList.groupBy {s =>
	Station(s(9), prefMap.get(s(10)), s(5))
}.toList.sortBy(_._2.length * -1) take 10

list.foreach {s =>
	printf("%s駅 (%s) : %d\n", s._1.stationName, s._1.prefName.get, s._2.length)
}
