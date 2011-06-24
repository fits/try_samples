
def f(x: Int, y: Int)(z: Int) = x + y + z

val f1: Int => Int = f(100, 20)

//z の値を指定
println(f1(3))

println(f(1, 2)(3))
