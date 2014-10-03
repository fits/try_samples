
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

case class Log(process: String, time: Int)

object LogCountSample extends App {

	val sc = new SparkContext("local[2]", "CoutupSample")

	val sqlContext = new SQLContext(sc)

	import sqlContext.createSchemaRDD

	val d = sc.textFile(args(0)).map(_.split("\t")).map { d =>
		Log(d(1).trim, d(0).trim.toInt)
	}

	d.registerTempTable("logs")

	val rows = sqlContext.sql("select process, count(*) c from logs where time >= 300 group by process")

	rows.map(r => s"*** process: ${r(0)}, count: ${r(1)}").collect().foreach(println)
}

