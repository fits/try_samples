
def plus = {x -> x + 3}
def times = {x -> x * 2}

def f = plus >> times
def g = plus << times

// times(plus(4)) = 14
println f(4)
// plus(times(4)) = 11
println g(4)
