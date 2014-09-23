
import sodium.*

def ba = new BehaviorSink('one')

def listener = ba.value().listen { a -> println "*** $a" }

ba.send 'two'
ba.send 'three'

listener.unlisten()
