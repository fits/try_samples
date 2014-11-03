
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext

import java.net.InetAddress

case class IpCountry(startIpNum: Long, endIpNum: Long, countryName: String)

object GetCountry extends App {
	if (args.length < 1) {
		println("<ip address>")
		System.exit(0)
	}

	val countryFile = "GeoIPCountryWhois.csv"

	val sc = new SparkContext("local", "GetCountry")

	val sqlContext = new SQLContext(sc)

	import sqlContext.createSchemaRDD

	val countries = sc.textFile(countryFile).map(_.replaceAll("\"", "").split(",")).map { d =>
		IpCountry(d(2).toLong, d(3).toLong, d(5))
	}

	countries.registerTempTable("countries")

	val ipNum = Integer.toUnsignedLong( InetAddress.getByName(args(0)).hashCode )

	val rows = sqlContext.sql(s"""
		select
			countryName
		from
			countries
		where
			startIpNum <= ${ipNum} and
			endIpNum >= ${ipNum}
	""")

	rows.foreach( r => println(r.head) )
}

