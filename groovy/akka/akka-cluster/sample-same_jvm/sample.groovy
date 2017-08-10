@Grab('com.typesafe.akka:akka-cluster_2.12:2.5.4')
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.UntypedActor

import akka.cluster.Cluster
import akka.cluster.ClusterEvent

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class SampleListener extends UntypedActor {
	def cluster = Cluster.get(getContext().system())

	void preStart() {
		println "*** preStart< ${cluster.selfAddress()} >:"

		cluster.subscribe(
			getSelf(), 
			ClusterEvent.initialStateAsEvents(), 
			ClusterEvent.MemberEvent, 
			ClusterEvent.UnreachableMember
		)
	}

	void postStop() {
		println "*** postStop< ${cluster.selfAddress()} >:"

		cluster.leave(cluster.selfAddress())
		cluster.unsubscribe(getSelf())
	}

	void onReceive(msg) {
		println "*** receive< ${cluster.selfAddress()} >: ${msg}"
	}
}

def systemName = 'sample1'

def sys1 = ActorSystem.create(systemName)

def joinAddr = Cluster.get(sys1).selfAddress()

Cluster.get(sys1).join(joinAddr)
sys1.actorOf(Props.create(SampleListener))

def sys2 = ActorSystem.create(systemName)
Cluster.get(sys2).join(joinAddr)
sys2.actorOf(Props.create(SampleListener))

def sys3 = ActorSystem.create(systemName)
Cluster.get(sys3).join(joinAddr)
sys3.actorOf(Props.create(SampleListener))

System.in.read()

Await.ready(
	sys1.terminate()
		.completeWith(sys2.terminate())
		.completeWith(sys3.terminate())
	,
	Duration.create(5, TimeUnit.SECONDS)
)
