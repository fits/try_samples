
def f(x: Int, y: Int, z: Int) = x + y + z

//カリー化関数に変換
val f1 = (f _).curried

println(f1(3)(7)(9))
