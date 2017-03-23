@Grab('com.typesafe.akka:akka-distributed-data_2.12:2.5.0-RC1')
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.AbstractActor
import akka.actor.AbstractActor.Receive

import akka.cluster.Cluster

import akka.cluster.ddata.PNCounter
import akka.cluster.ddata.PNCounterKey
import akka.cluster.ddata.DistributedData
import akka.cluster.ddata.Replicator

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class SampleListener extends AbstractActor {
	def cluster = Cluster.get(getContext().system())

	def replicator = DistributedData.get(getContext().system()).replicator()
	def key = PNCounterKey.create('sample-counter')

	void preStart() {
		def subscribe = new Replicator.Subscribe(key, getSelf())
		replicator.tell(subscribe, ActorRef.noSender())
	}

	void postStop() {
		def unsubscribe = new Replicator.Unsubscribe(key, getSelf())
		replicator.tell(unsubscribe, ActorRef.noSender())

		cluster.leave(cluster.selfAddress())
	}

	Receive createReceive() {
		receiveBuilder()
			.matchEquals('dump') {
				println "**** get <${info()}>"

				def consist = Replicator.readLocal()
				//def consist = new Replicator.ReadAll(Duration.create(5, TimeUnit.SECONDS))

				def get = new Replicator.Get(key, consist)

				replicator.tell(get, getSelf())
			}
			.match(Integer) { count ->
				println "**** update <${info()}> : ${count}"

				def consist = Replicator.writeLocal()
				//def consist = new Replicator.WriteAll(Duration.create(5, TimeUnit.SECONDS))

				def update = new Replicator.Update(
					key, 
					PNCounter.create(), 
					consist,
					{ cur -> cur.increment(cluster, count) }
				)

				replicator.tell(update, getSelf())
			}
			.match(Replicator.UpdateResponse) {
				println "*** UpdateResponse <${info()}> : ${it}"
			}
			.match(Replicator.GetSuccess) {
				println "*** GetSuccess <${info()}> : ${it}, value: ${it.dataValue()}"
			}
			.match(Replicator.Changed) {
				println "*** Changed <${info()}>: ${it}, value: ${it.dataValue()}"
			}
			.matchAny {
				println "*** other <${info()}> : ${it}"
			}
			.build()
	}

	private String info() {
		"${getSelf()}, ${cluster.selfAddress()}"
	}
}

def systemName = 'sample1'

def sys1 = ActorSystem.create(systemName)

def joinAddr = Cluster.get(sys1).selfAddress()

Cluster.get(sys1).join(joinAddr)
def actor1 = sys1.actorOf(Props.create(SampleListener), 'act1')

def sys2 = ActorSystem.create(systemName)
Cluster.get(sys2).join(joinAddr)
def actor2 = sys2.actorOf(Props.create(SampleListener), 'act2')

def sys3 = ActorSystem.create(systemName)
Cluster.get(sys3).join(joinAddr)
def actor3 = sys3.actorOf(Props.create(SampleListener), 'act3')

Thread.sleep(3000)

(0..<10).each {
	actor1.tell(it, ActorRef.noSender())
}

actor1.tell('dump', ActorRef.noSender())
actor2.tell('dump', ActorRef.noSender())
actor3.tell('dump', ActorRef.noSender())

Thread.sleep(1000)

actor1.tell('dump', ActorRef.noSender())
actor2.tell('dump', ActorRef.noSender())
actor3.tell('dump', ActorRef.noSender())

System.in.read()

Await.ready(
	sys1.terminate()
		.completeWith(sys2.terminate())
		.completeWith(sys3.terminate())
	,
	Duration.create(5, TimeUnit.SECONDS)
)
