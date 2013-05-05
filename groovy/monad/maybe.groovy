
import groovy.transform.*

class Maybe<T> {
	T value
}

class Nothing extends Maybe<Void> {
	static Nothing instance = new Nothing()

	private Nothing() {
	}
}

class Just<T> extends Maybe<T> {
}

class MaybeMonad {
	static <T> Maybe<T> just(T self) {
//	self Ç™ null ÇÃèÍçáÇ≈Ç‡ self == null ÇÕ false Ç∆Ç»ÇÈ
//		if (self == null) {
		if (self.toString() == 'null') {
			nothing()
		}
		else {
			new Just(value: self)
		}
	}

	static <T> Maybe<T> nothing(T self) {
		Nothing.instance
	}

	static <T, V> Maybe<V> bind(Maybe<T> self, Closure<Maybe<V>> k) {
		if (self instanceof Just) {
			k(self.value)
		}
		else {
			self
		}
	}
}

println new Just(value: "test")
println Nothing.instance

println "-------------"

use(MaybeMonad) {
	println null.just()
	println null.nothing()
	println 1.nothing()
	println "abc".just()

	println "---"

	def res1 = 10.just() bind { (it * 2).just() } bind { (it + '!!!').just() }

	println res1
	println res1.value

	def res2 = 10.just() bind { it.nothing() } bind { (it + '!!!').just() }

	println res2
	println res2.value
}
