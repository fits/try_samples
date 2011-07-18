import scala.io.Source

val prefMap = Source.fromFile("m_pref.csv").getLines().drop(1).map {l =>
	val items = l.split(",")
	items(0) -> items(1)
}.toMap

val lines = Source.fromFile("m_station.csv").getLines()

val list = lines.drop(1).toList.map(_.split(",")).groupBy {s =>
	(s(9), prefMap.get(s(10)).get, s(5))
}.toList.sortWith {(a, b) => 
	a._2.length > b._2.length
} take 10

list.foreach {s =>
	printf("%s駅 (%s) : %d\n", s._1._1, s._1._2, s._2.length)
}
