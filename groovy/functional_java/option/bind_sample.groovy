@Grab("org.functionaljava:functionaljava:3.1")
import fj.F
import fj.F2
import static fj.data.List.*
import static fj.data.Option.*

def plus = { int x -> some(x + 3) } as F
def times = { int x -> some(x * 2) } as F

// some(14)
println some(4).bind(plus).bind(times)


// some(14)
println some(4).bind(list(plus, times).foldLeft1({b, a -> 
	{ x -> b.f(x).bind(a) } as F
} as F2))


// some(11)
println some(4).bind(list(plus, times).foldLeft1({b, a -> 
	{ x -> a.f(x).bind(b) } as F
} as F2))


// some(14)
println some(4).bind(list(plus, times).foldRight({a, b ->
	{ x -> a.f(x).bind(b) } as F
} as F2, { y -> some(y) } as F))


// some(11)
println some(4).bind(list(plus, times).foldRight({a, b ->
	{ x -> b.f(x).bind(a) } as F
} as F2, { y -> some(y) } as F))


// some(28)
println some(4).bind(list(plus, times, times).foldLeft1({b, a -> 
	{ x -> b.f(x).bind(a) } as F
} as F2))

