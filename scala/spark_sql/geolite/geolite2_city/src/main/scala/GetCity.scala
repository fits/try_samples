
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

import java.net.InetAddress

case class IpMapping(startIpNum: Long, endIpNum: Long, geonameId: String)

case class City(geonameId: String, country: String, city: String)

object GetCity extends App {

	if (args.length < 1) {
		println("<ip address>")
		System.exit(0)
	}

	val toIpNum = (ip: String) => Integer.toUnsignedLong(InetAddress.getByName(ip).hashCode())

	val locationFile = "GeoLite2-City-Locations.csv"
	val blockFile = "GeoLite2-City-Blocks.csv"

	val sc = new SparkContext("local[2]", "GetCity")

	val sqlContext = new SQLContext(sc)

	import sqlContext.createSchemaRDD

	val locations = sc.textFile(locationFile).map(_.split(",")).map { r =>
		val city = if (r.length > 7) r(7) else ""
		City(r(0), r(4), city)
	}

	locations.registerTempTable("locations")

	val blocks = sc.textFile(blockFile).filter(_.startsWith("::ffff:")).map(_.split(",")).map { r =>
		val mask = -1 << (128 - r(1).toInt)

		val startIpNum = toIpNum(r(0).replaceAll("::ffff:", ""))
		val endIpNum = startIpNum | ~mask

		IpMapping(startIpNum, endIpNum, r(2))
	}

	blocks.registerTempTable("blocks")

	val ipNum = toIpNum(args(0))

	val rows = sqlContext.sql(s"select city, country from locations lo join blocks bl on bl.geonameId = lo.geonameId where startIpNum <= ${ipNum} and endIpNum >= ${ipNum}")

	rows.foreach( r => println(s"${r(0)}, ${r(1)}") )
}

