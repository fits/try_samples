def f = {x, y, z ->  x + y + z}

//x と y の値が指定された状態
def f1 = f.curry(100, 20)

println f1(3)

//x の値を指定した状態
def f2 = f.curry(1)

println f2(20, 300)

//f2 に y の値を指定した状態
def f3 = f2.curry(20)

println f3(3000)
