
val lessTen: PartialFunction[Int, String] = {
	case x if x < 10 => s"$x < 10"
}

val lessTwenty: PartialFunction[Int, String] = {
	case x if x < 20 => s"$x < 20"
}

val other: PartialFunction[Int, String] = {
	case x => s"$x is other"
}

val check = lessTen orElse lessTwenty orElse other

println(check(0))
println(check(10))
println(check(15))
println(check(20))
println(check(30))
