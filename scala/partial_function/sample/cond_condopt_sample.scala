import PartialFunction._

val check = (x: Int) => condOpt(x) {
	case x if x < 10 => s"$x < 10"
	case x if x < 20 => s"$x < 20"
}

val printCheck = (x: Int) => println(check(x) getOrElse s"$x is other")

printCheck(0)
printCheck(10)
printCheck(15)
printCheck(20)
printCheck(30)


val checkAny = (x: Any) => cond(x) {
	case x: Int if x < 10 => true
	case x: String if x == "sample" => true
}

println(checkAny(1))
println(checkAny(10))

println(checkAny("sample"))
println(checkAny("test"))

println(checkAny((x: Int) => x + 10))
