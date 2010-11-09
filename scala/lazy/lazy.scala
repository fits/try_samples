
var n = 10

val x1 = n + 100
lazy val x2 = n + 100

n = 1

//x1=110, x2=101
println("x1=" + x1 + ", x2=" + x2)

n = 5

//x1=110, x2=101
println("x1=" + x1 + ", x2=" + x2)


def f(x: int)(y: int): int = x + y
def g(x: => int)(y: int): int = x + y

val f1: int => int = f(n)
val g1: int => int = g(n)

n = 3

println("f1=" + f1(2) + ", g1=" + g1(2))
