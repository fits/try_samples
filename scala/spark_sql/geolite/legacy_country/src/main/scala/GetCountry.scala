
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

import java.net.InetAddress

case class IpCountry(startIpNum: Long, endIpNum: Long, countryName: String)

object GetCountry extends App {
	if (args.length < 2) {
		println("<geolite country csv file> <ip address>")
		System.exit(0)
	}

	val sc = new SparkContext("local[2]", "GetCountry")

	val sqlContext = new SQLContext(sc)

	import sqlContext.createSchemaRDD

	val d = sc.textFile(args(0)).map(_.replaceAll("\"", "").split(",")).map { d =>
		IpCountry(d(2).toLong, d(3).toLong, d(5))
	}

	d.registerTempTable("countries")

	val ipNum = Integer.toUnsignedLong( InetAddress.getByName(args(1)).hashCode )

	val rows = sqlContext.sql(s"select countryName from countries where startIpNum <= ${ipNum} and endIpNum >= ${ipNum}")

	rows.foreach( r => println(r.head) )
}

