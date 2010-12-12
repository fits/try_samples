
val plus = (x: Int) => x + 3
val times = (x: Int) => x * 2

val f = plus andThen times
val g = plus compose times

// times(plus(4)) = 14
println(f(4))
// plus(times(4)) = 11
println(g(4))
