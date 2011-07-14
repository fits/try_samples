import scala.io.Source

case class Station(val prefName: Option[String], val stationGroupCode: String, val stationName: String)

val prefMap = Source.fromFile("m_pref.csv").getLines().drop(1).map {l =>
	val items = l.split(",")
	items(0) -> items(1)
}.toMap

val lines = Source.fromFile("m_station.csv").getLines()

val list = lines.drop(1).map {l =>
	val items = l.split(",")
	Station(prefMap.get(items(10)), items(5), items(9))
}.toList.groupBy {s =>
	(s.stationGroupCode, s.stationName, s.prefName)
}.toList.sortBy(_._2.length * -1) take 10

list.foreach {l =>
	printf("%s駅 (%s) : %d\n", l._1._2, l._1._3.get, l._2.length)
}

