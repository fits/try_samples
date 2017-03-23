@Grab('com.typesafe.akka:akka-cluster-sharding_2.12:2.5.0-RC1')
@Grab('com.github.jnr:jffi:1.2.15')
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.ActorRef
import akka.actor.AbstractActor.Receive
import akka.actor.ReceiveTimeout
import akka.actor.PoisonPill

import akka.cluster.Cluster
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ClusterShardingSettings

import akka.persistence.AbstractPersistentActor
import akka.persistence.SnapshotOffer

import groovy.transform.Immutable

@Immutable
class CounterAdd implements Serializable {
	String id
	int count
}

@Immutable
class AddedCounter implements Serializable {
	String id
	int count
}

@Immutable
class DumpCounter implements Serializable {
	String id
}

class CounterActor extends AbstractPersistentActor {
	def cluster = Cluster.get(getContext().system())
	int state = 0

	String persistenceId() {
		"Counter-${getSelf().path().name()}"
	}

	void postStop() {
		println "*** postStop <${info()}>"
		cluster.leave(cluster.selfAddress())
	}

	Receive createReceive() {
		receiveBuilder()
			.match(CounterAdd) { cmd -> 
				println "*** CounterAdd <${info()}> : ${cmd}"

				persist(new AddedCounter(cmd.id, cmd.count)) { ev ->
					updateState(ev)
					context().system().eventStream().publish(ev)
				}
			}
			.match(ReceiveTimeout) {
				println '*** ReceiveTimeout <${info()}>'
				passivate()
			}
			.match(DumpCounter) { cmd -> 
				println "*** DumpCounter <${info()}> : ${state}"
			}
			.matchAny {
				println "*** other <${info()}> : ${it}"
			}
			.build()
	}

	Receive createReceiveRecover() {
		receiveBuilder()
			.match(AddedCounter) { ev -> 
				updateState(ev)
			}
			.match(SnapshotOffer) { ss -> 
				state = ss.snapshot()
			}
			.build()
	}

	private void updateState(event) {
		state += event.count
	}

	private void passivate() {
		println "*** passivate <${info()}>"

		getContext().parent.tell(
			new ShardRegion.Passivate(PoisonPill.getInstance()),
			getSelf()
		)
	}

	private String info() {
		"${getSelf()}, ${cluster.selfAddress()}"
	}
}

class CounterMessageExtractor extends ShardRegion.HashCodeMessageExtractor {
	CounterMessageExtractor(maxShardsNum) {
		super(maxShardsNum)
	}

	String entityId(msg) {
		println "*** entityId <${this}> : ${msg}"
		msg.id
	}
}

def systemName = 'sample1'

def sys1 = ActorSystem.create(systemName)

def joinAddr = Cluster.get(sys1).selfAddress()

Cluster.get(sys1).join(joinAddr)

def sys2 = ActorSystem.create(systemName)
Cluster.get(sys2).join(joinAddr)

Thread.sleep(3000)

def shardList = [sys1, sys2].collect {
	def settings = ClusterShardingSettings.create(it)

	ClusterSharding.get(it).start(
		'Counter', 
		Props.create(CounterActor), 
		settings, 
		new CounterMessageExtractor(2)
	)
}

def actor = shardList.first()

actor.tell(new CounterAdd('d1', 3), ActorRef.noSender())
actor.tell(new CounterAdd('d1', 5), ActorRef.noSender())

actor.tell(new CounterAdd('d2', 11), ActorRef.noSender())
actor.tell(new CounterAdd('d2', 100), ActorRef.noSender())

actor.tell(new CounterAdd('d3', 77), ActorRef.noSender())

actor.tell(new DumpCounter('d1'), ActorRef.noSender())
actor.tell(new DumpCounter('d2'), ActorRef.noSender())
actor.tell(new DumpCounter('d3'), ActorRef.noSender())

System.in.read()

shardList.each {
	it.tell(ShardRegion.gracefulShutdownInstance(), ActorRef.noSender())
}
