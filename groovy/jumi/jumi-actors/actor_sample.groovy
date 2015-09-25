//@Grab('fi.jumi.actors:jumi-actors:1.0.277')
import fi.jumi.actors.MultiThreadedActors
import fi.jumi.actors.eventizers.dynamic.DynamicEventizerProvider
import fi.jumi.actors.listeners.CrashEarlyFailureHandler
import fi.jumi.actors.listeners.NullMessageListener

import java.util.concurrent.Executors

def es = Executors.newCachedThreadPool()

def actors = new MultiThreadedActors(
	es,
	new DynamicEventizerProvider(),
	new CrashEarlyFailureHandler(),
	new NullMessageListener()
)

def actor = actors.startActorThread()

interface Sample {
	void sample(String msg)
}

def sampleRef = actor.bindActor(Sample, { msg -> println "received: ${msg}"} as Sample)

(1..10).each {
	sampleRef.tell().sample "msg${it}"
}

actor.stop()

es.shutdown()
