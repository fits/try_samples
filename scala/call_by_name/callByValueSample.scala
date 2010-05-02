
class When2(a: Any) {
	def when(b: Boolean) = if(b) a
}

object CallByValueSample {
	implicit def toWhen(a: Any) = new When2(a)
}

