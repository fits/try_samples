import scala.io.Source

case class Station(val prefCode: String, val stationGroupCode: String, val stationName: String)

val stationFile = "m_station.csv"

val lines = Source.fromFile(stationFile, "MS932").getLines()

val list = lines.drop(1).map {l =>
	val items = l.split(",")
	Station(items(10), items(5), items(9))
}.toList.groupBy(s => (s.stationGroupCode, s.stationName)).toList.sortBy(x => x._2.length * -1).take(10)


list.foreach {l =>
	printf("%s駅 : %d\n", l._1._2, l._2.length)
}
