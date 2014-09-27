
import sodium.*

def ba1 = new BehaviorSink(0)
def ba2 = new BehaviorSink(100)

def ev1 = ba1.value().merge(ba2.value())
def ev2 = ba1.value().snapshot(new Behavior(3)) { a, b -> a * b }
def ev3 = ba1.value().snapshot(ba2) { a, b -> a + b }

def li = ev1.listen { a -> println "*** $a" }
li = li.append ev2.listen { a -> println "--- $a" }
li = li.append ev3.listen { a -> println "+++ $a" }

(1..5).each {
	ba1.send it
	ba2.send it * 100
}

li.unlisten()
