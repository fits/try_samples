import scala.math._

type Vec = Map[String, Double]

val innerProd = (a: Vec, b: Vec) => {
	a.toList.map {f =>
		f._2 * b.getOrElse(f._1, 0.0)
	}.foldLeft(0.0)(_ + _)
}

val vecLen = (a: Vec) => {
	a.values.foldLeft(0.0) {(x, y) =>
		x + pow(y, 2)
	}
}

val cosineSim = (a: Vec, b: Vec) => {
	innerProd(a, b) / sqrt(vecLen(a) * vecLen(b))
}

val data1 = Map(
	"test1" -> 5.0,
	"test2" -> 2.0,
	"test3" -> 2.0
)

val data2 = Map(
	"test2" -> 5.0,
	"test3" -> 3.0
)

val data3 = Map(
	"test2" -> 1.0,
	"test3" -> 3.0
)

println("data1 : data2 = " + cosineSim(data1, data2))
println("data2 : data3 = " + cosineSim(data2, data3))
println("data1 : data3 = " + cosineSim(data1, data3))

