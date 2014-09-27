
import sodium.*

def ba = new BehaviorSink('one')

// include current value
def li = ba.value().listen { a -> println "*** $a" }
// exclude current value (only new value)
li = li.append ba.updates().listen { a -> println "--- $a" }

println ba.sample()

ba.send 'two'

println ba.sample()

ba.send 'three'

println ba.sample()

li.unlisten()
