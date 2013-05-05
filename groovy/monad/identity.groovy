
import groovy.transform.*

class Identity<T> {
	T value
}

class IdentityProc {
	static <T> Identity unit(T value) {
		new Identity(value: value)
	}

	static <T, V> Identity bind(Identity<T> id, Closure<V> k) {
		k(id.value)
	}
}

Identity.metaClass.bind = { Closure k ->
	IdentityProc.bind(delegate, k)
}


class IdentityMonad {
	static <T> Identity<T> identity(T self) {
		IdentityProc.unit(self)
	}

	static <T, V> Identity<T> bind(Identity<T> self, Closure<V> k) {
		IdentityProc.bind(self, k)
	}
}

import static IdentityProc.*

println unit("a")

def res = bind(unit(10)) { v ->
	unit(v + "!!!")
}

println res.value

def res2 = unit(10) bind { unit(it + 5) } bind { unit(it + '!!!') }

println res2.value

use(IdentityMonad) {
	def res3 = 10.identity() bind { (it * 5).identity() } bind { (it + '!!!').identity() }

	println res3.value
}

