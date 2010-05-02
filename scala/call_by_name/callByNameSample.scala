
class When(a: => Any) {
	def when(b: Boolean) = if(b) a
}

object CallByNameSample {
	implicit def toWhen(a: => Any) = new When(a)
}

