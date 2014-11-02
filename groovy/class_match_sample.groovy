
import groovy.transform.TupleConstructor

@TupleConstructor
class Case {
	Class type
	Closure proc
}

//def match = { Case... cases ->
def match(Case... cases) {
	return { obj ->
		cases.inject( null ) { acc, cs ->
			if (acc == null && cs.type.isInstance(obj)) {
				acc = cs.proc(obj)
			}
			acc
		}
	}
}

//def tcase = { Class type, Closure proc ->
def tcase(Class type, Closure proc) {
	new Case(type, proc)
}

def f1 = match(
	tcase(String) { "str: $it" },
	tcase(Date) { "date: $it" }
)

println f1('test')
println f1(new Date())

println '---'

def f2 = null
f2 = match(
	tcase(String) { "str: $it" },
	tcase(Date) { "date: $it" },
	tcase(List) { it.collect { v -> f2(v) } }
)

def f3(obj) {
	match(
		tcase(String) { "str: $it" },
		tcase(Date) { "date: $it" },
		tcase(List) { it.collect { v -> f3(v) } }
	).call(obj)
}

println '---'

println f2('test')
println f2(new Date())
println f2( ['a', 'b', new Date()] )

println '---'

println f3( ['a', 'b', new Date()] )

