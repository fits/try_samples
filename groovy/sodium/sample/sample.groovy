
import sodium.*

def ba = new BehaviorSink('one')

// include current value
def l1 = ba.value().listen { a -> println "*** $a" }
// exclude current value (only new value)
def l2 = ba.updates().listen { a -> println "--- $a" }

println ba.sample()

ba.send 'two'

println ba.sample()

ba.send 'three'

println ba.sample()

l1.unlisten()
l2.unlisten()
