
import sodium.*

def ba1 = new BehaviorSink(0)
def ba2 = new BehaviorSink(100)

def ev1 = ba1.value().merge(ba2.value())
def ev2 = ba1.value().snapshot(new Behavior(3)) { a, b -> a * b }
def ev3 = ba1.value().snapshot(ba2) { a, b -> a + b }

def l1 = ev1.listen { a -> println "*** $a" }
def l2 = ev2.listen { a -> println "--- $a" }
def l3 = ev3.listen { a -> println "+++ $a" }

(1..5).each {
	ba1.send it
	ba2.send it * 100
}

l1.unlisten()
l2.unlisten()
l3.unlisten()
