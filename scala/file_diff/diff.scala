
import scala.io.Source

//2.8rc1
//val lines = Source.fromPath(args(0))("UTF-8").getLines().foreach(println)

val srcList = Source.fromPath(args(0)).getLines().toList
val trgList = Source.fromPath(args(1)).getLines().toList

//diff
srcList.diff(trgList).foreach(println)

println("------------")

trgList.diff(srcList).foreach(println)

