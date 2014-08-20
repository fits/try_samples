@Grab("org.functionaljava:functionaljava:4.2-beta-1")
import fj.data.Either

def f1 = {
	println 'call f1'
	Date.parse('yyyy/MM/dd HH:mm:ss', it)
}

def f2 = {
	println 'call f2'
	Date.parse('yyyy-MM-dd HH:mm:ss', it)
}

def f3 = {
	println 'call f3'
	Date.parse('yyyy-MM-dd', it)
}

def liftE = { f ->
	return {
		try {
			Either.right( f(it) )
		} catch (e) {
			println e.message
			Either.left(it)
		}
	}
}

def data = Either.left(args[0])

def res = [ f1, f2, f3 ].inject(data) { acc, f ->
	acc.left().bind liftE(f)
}

println '-----'
println res
