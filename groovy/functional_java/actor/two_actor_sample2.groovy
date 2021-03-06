@Grab('org.functionaljava:functionaljava:4.4')
import fj.control.parallel.Actor
import fj.control.parallel.Strategy
import fj.function.Effect1

import java.util.concurrent.Executors

def es = Executors.newCachedThreadPool()
def strategy = Strategy.executorStrategy(es)

def actor1 = Actor.actor(strategy, { a -> println "received : ${a}" } as Effect1)
def actor2 = Actor.actor(strategy, { a ->
	println a
	actor1.act("${a}!!!")
} as Effect1)


(1..10).each {
	actor2.act "msg-${it}"
}

es.shutdown()
