
def f(x: int, y: int)(z: int) = x + y + z

val f1: int => int = f(100, 20)

//z の値を指定
println(f1(3))

println(f(1, 2)(3))
