
class Input<E> {
	E value
}

final class EOF<E> extends Input<E> {
}

final class Empty<E> extends Input<E> {
}

final class El<E> extends Input<E> {
	El(E value) {
		this.value = value
	}
}

class IterV<E, A> {
}

final class Cont<E, A> extends IterV<E, A> {
	Closure<IterV<E, A>> result
}

final class Done<E, A> extends IterV<E, A> {
	A result
	Input<E> rest
}

def <E, A> IterV<E, A> enumerator(IterV<E, A> iter, E[] el) {
	println "*** cont : ${el}"

	if (el.length == 0 || iter instanceof Done) {
		iter
	}
	else if (iter instanceof Cont) {
		enumerator(iter.result(new El(el.head())), el.tail())
	}
}

def <E, A> A run(IterV<E, A> iter) {
	if (iter instanceof Done) {
		iter.result
	}
	else if (iter instanceof Cont) {
		def res = iter.result(new EOF<E>())

		if (res instanceof Done) {
			res.result
		}
		else {
			null
		}
	}
}

println new EOF()
println new El('sample')


def <E, A> IterV<E, A> head() {
	def stepHead

	stepHead = { Input<E> el ->
		println "*** ${this} ${owner}"

		if (el instanceof EOF) {
			new Done<E, A>(rest: new Empty<E>())
		}
		else if (el instanceof Empty) {
			new Cont<E, A>(result: stepHead)
		}
		else if (el instanceof El) {
			new Done<E, A>(result: el.value, rest: new Empty<E>())
		}
	}

	new Cont<E, A>(result: stepHead)
}

def <E, A> IterV<E, A> skip() {
	def stepSkip

	stepSkip = { Input<E> el ->
		if (el instanceof EOF) {
			new Done<E, A>(rest: new Empty<E>())
		}
		else if (el instanceof Empty) {
			new Cont<E, A>(result: stepSkip)
		}
		else if (el instanceof El) {
			new Done<E, A>(rest: new Empty<E>())
		}
	}
	new Cont<E, A>(result: stepSkip)
}

def <E, A, R> IterV<A, R> bind(IterV<E, A> iter, Closure<IterV<A, R>> f) {
	if (iter instanceof Done) {
		def res = f(iter.result)

		if (res instanceof Done) {
			new Done(result: res.result, rest: iter.rest)
		}
		else if (res instanceof Cont) {
			res.result(iter.rest)
		}
	}
	else if (iter instanceof Cont) {
		new Cont(result: { str ->
			bind(iter.result(str), f)
		})
	}
}

def <E, A> IterV<List<E>, A> take(int n) {
	def step

	step = { int count, List<E> acc, Input<E> el ->
		if (el instanceof EOF) {
			new Done(result: acc, rest: new Empty())
		}
		else if (el instanceof Empty) {
			new Cont(result: step.curry(count, acc))
		}
		else if (el instanceof El) {
			println "---- ${el.value}"

			(count == 1)? new Done(result: acc << el.value, rest: new Empty()): new Cont(result: step.curry(count - 1, acc << el.value))
		}
	}
	println "** ${n}"

	(n == 0)? new Done(result: [], rest: new EOF()): new Cont(result: step.curry(n, []))
}


println run(enumerator(head(), 'a1', 'b2', 'c3'))

println run(enumerator(skip(), 'a1', 'b2', 'c3'))

println run(enumerator(bind(skip(), { head() }), 'a1', 'b2', 'c3'))

println run(enumerator(bind(skip(), { take(2) }), 'a1', 'b2', 'c3', 'd4'))
