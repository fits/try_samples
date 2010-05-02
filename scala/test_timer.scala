
object TestTimer {
	def exec(callback: () => unit) {
		while (true) {
			callback()
			Thread sleep 1000
		}
	}

	def main(args: Array[String]) {
		exec(() =>
			println("on time")
		)
	}
}

TestTimer.main(null)
