
import sodium.*

def line = new BehaviorSink(null)

def skip = { Event ev ->
	def skipper = new BehaviorSink(false)
	def li = null

	li = ev.once().listen {
		skipper.send true
		li?.unlisten()
	}

	ev.gate skipper
}

def take = { int n, Event ev ->
	def counter = new BehaviorSink(n)
	def li = null

	li = ev.listen {
		def newValue = counter.sample() - 1
		counter.send newValue
	
		if (newValue <= 0) {
			li?.unlisten()
		}
	}

	ev.gate counter.map { it > 0 }
}

def skipAndTake3 = skip.curry() >> take.curry(3)

def li = skipAndTake3(line.updates()).map { "# ${it}" }.listen { println it }

new File(args[0]).eachLine { line.send it }

li.unlisten()
