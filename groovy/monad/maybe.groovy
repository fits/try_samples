
abstract class Maybe<T> {
	abstract T value(T defaultValue)
}

final class Nothing<T> extends Maybe<T> {
	@Override T value(T defaultValue) {
		defaultValue
	}
}

final class Just<T> extends Maybe<T> {
	private T value

	Just(T value) {
		this.value = value
	}

	@Override T value(T defaultValue = null) {
		this.value
	}
}

class MaybeMonad {
	static <T> Maybe<T> just(T self) {
//	self ‚ª null ‚Ìê‡‚É self == null ‚ª false ‚Æ‚È‚é–Í—l
//		if (self == null) {
		if (self.toString() == 'null') {
			nothing()
		}
		else {
			new Just(self)
		}
	}

	static <T> Maybe<T> nothing(T self = null) {
		new Nothing<T>()
	}

	static <T, V> Maybe<V> bind(Maybe<T> self, Closure<Maybe<V>> k) {
		if (self instanceof Just) {
			k(self.value())
		}
		else {
			self
		}
	}
}

println new Just("test")
println new Nothing()

println "-------------"

use(MaybeMonad) {
	println null.just()
	println null.nothing()
	println 1.nothing()
	println "abc".just()

	println "---"

	def res1 = 10.just() bind { (it * 2).just() } bind { (it + '!!!').just() }

	println res1
	println res1.value()

	def res2 = 10.just() bind { it.nothing() } bind { (it + '!!!').just() }

	println res2
	println res2.value('none')

	def res3 = new Date().just() bind { it.format('yyyy/MM/dd HH:mm:ss').just() }
	println res3.value('none')
}
