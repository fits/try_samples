
val plus = (x: Int) => x + 3
val times = (x: Int) => x * 2

val f = plus andThen times
// times(plus(4))
println(f(4))

val g = plus compose times
// plus(times(4))
println(g(4))

