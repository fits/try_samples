@Grab('org.functionaljava:functionaljava:4.4')
import fj.data.Writer
import fj.data.List
import fj.Monoid

def w = Writer.unit("sample", Monoid.listMonoid())

def func = { newValue, oldValue ->
	Writer.unit(newValue, List.single(oldValue), Monoid.listMonoid())
}

w = w.flatMap(func.curry('abc')).flatMap(func.curry('test'))

println w.run()
