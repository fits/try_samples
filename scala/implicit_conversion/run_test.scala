
class RunnableWrap[A](f: A) extends Runnable {
	def run {
		println("run")
		f
	}
}

implicit def toRunnableWrap[A](f: A) = new RunnableWrap(f)

new Thread(for(i <- 1 to 5) println(i)) start
