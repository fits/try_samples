
def calc(x: Int, y: Int)(z: Int) = (x * z) + (y * z)

val c: Int => Int = calc(100, 10)

println(c(5))
