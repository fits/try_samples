
def calc(x: int, y: int)(z: int) = (x * z) + (y * z)

val c: int => int = calc(100, 10)

println(c(5))
