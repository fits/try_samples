
import sodium.*

def a1 = new BehaviorSink(0)
def b1 = new BehaviorSink(0)

def add = { a, b -> a + b }

def c1 = a1.lift(add, b1)

def li = c1.value().listen { a -> println "c1 = $a" }

println '--- a1.send 10'
a1.send 10

println '--- b1.send 2'
b1.send 2

println '--- a1.send 30'
a1.send 30

println '--- b1.send 4'
b1.send 4

li.unlisten()
