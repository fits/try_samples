import scala.xml._

case class HttpSample(val name: String, val rc: String, val time: Int)

val doc = XML.loadFile(args(0))

val list = (doc \\ "httpSample").map {s =>
	val tn = (s \ "@tn").head.text
	val rc = (s \ "@rc").head.text
	val t = (s \ "@t").head.text.toInt

	HttpSample(tn, rc, t)
}

println(s"all : ${list.size}")

val lateList = list.filter(_.time >= 3000)
println(s"late list : ${lateList.size}")

//lateList.foreach(println)

val total = list.foldLeft(0)(_ + _.time)
println(s"total time : ${total}")
