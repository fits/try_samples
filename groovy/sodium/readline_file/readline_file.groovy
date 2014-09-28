
import sodium.*

def line = new BehaviorSink(null)

def skip = { Event ev ->
	def sk = new BehaviorSink(false)

	def li = ev.once().listen { sk.send true }
	ev.addCleanup li

	ev.gate(sk)
}

def take = { int n, Event ev ->
	def counter = new BehaviorSink(n)

	def li = ev.listen { counter.send( counter.sample() - 1 ) }
	ev.addCleanup li

	ev.gate(counter.map { v -> v > 0 })
}

def skipAndTake3 = skip.curry() >> take.curry(3)

def li = skipAndTake3(line.updates()).map { "# ${it}" }.listen { println it }

new File(args[0]).eachLine { line.send it }

li.unlisten()
