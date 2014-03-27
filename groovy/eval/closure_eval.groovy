
import groovy.util.Eval

def plus = { a, b -> a + b }
def times = { a, b -> a * b }

println this

Eval.me('println this')
Eval.x(this, 'println x')

def plus2 = Eval.x(plus, 'x.curry(2)')
println plus2(5)

// ‰º‹L‚Í•s‰Â
//def times3 = Eval.x(this, 'x.times.curry(3)')

def times3 = Eval.x([plus, times], 'x[1].curry(3)')
println times3(3)

def func = Eval.xy(plus, times, 'x.curry(2) >> y.curry(3)')
println func(4)

def func2 = Eval.me('{ a, b -> a / b }')
println func2.call(10, 5)
