
import sodium.*

def ba = new BehaviorSink(0)
def ba2 = ba.map { it * 10 }
def ba3 = new Behavior(3)

def ba4 = ba2.lift({ a, b ->
	a + b
}, ba3)

def li = ba4.value().listen { a -> println "*** $a" }

(1..5).each {
	ba.send it
}

li.unlisten()
