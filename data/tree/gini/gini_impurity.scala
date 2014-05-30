
import scala.math.pow

val calcGini = (list: List[_]) => 1 - list.groupBy(identity).mapValues( v => pow(v.size.toDouble / list.size, 2) ).values.sum

val calcGini2 = (list: List[_]) => list.groupBy(identity).mapValues( v => v.size.toDouble / list.size).toList.combinations(2).map( x => x.head._2 * x.last._2 * 2 ).sum

val list = List("A", "B", "B", "C", "A", "B")

println(calcGini(list))
println(calcGini2(list))
