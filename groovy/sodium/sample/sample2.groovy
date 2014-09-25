
import sodium.*

def ba = new BehaviorSink(0)
def ba2 = ba.map { it * 10 }

def l1 = ba2.value().listen { a -> println "*** $a" }

(1..5).each {
	ba.send it
}

l1.unlisten()
