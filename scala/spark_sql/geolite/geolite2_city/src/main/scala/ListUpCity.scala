
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

import java.net.InetAddress

object ListUpCity extends App {
	if (args.length < 1) {
		println("<country name>")
		System.exit(0)
	}

	case class City(geonameId: String, country: String, city: String)

	val locationFile = "GeoLite2-City-Locations.csv"

	val sc = new SparkContext("local[2]", "ListUpCity")

	val sqlContext = new SQLContext(sc)

	import sqlContext.createSchemaRDD

	val locations = sc.textFile(locationFile).map(_.split(",")).map { r =>
		val city = if (r.length > 7) r(7) else ""
		City(r(0), r(4), city)
	}

	locations.registerTempTable("locations")

	val rows = sqlContext.sql(s"select city from locations where country = '${args(0)}'")

	rows.foreach( r => println(r.head) )
}

