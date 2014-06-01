import scala.math.pow

// (a) 1 - (AA + BB + CC)
val giniA = (xs: List[_]) => 1 - xs.groupBy(identity).mapValues( v =>
	pow(v.size.toDouble / xs.size, 2)
).values.sum

// (b) AC × 2 + AB × 2 + CB × 2
val giniB = (xs: List[_]) => xs.groupBy(identity).mapValues( v =>
	v.size.toDouble / xs.size
).toList.combinations(2).map( x =>
	x.head._2 * x.last._2 * 2
).sum

val list = List("A", "B", "B", "C", "B", "A")

println( giniA(list) )
println( giniB(list) )
