@Grab('com.typesafe.akka:akka-cluster_2.12:2.5.0')
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
		println '*** preStart'

		cluster.subscribe(
			getSelf(), 
			ClusterEvent.initialStateAsEvents(), 
			ClusterEvent.MemberEvent, 
			ClusterEvent.UnreachableMember
		)
	}

	void postStop() {
		println '*** postStop'

		cluster.leave(cluster.selfAddress())
		cluster.unsubscribe(getSelf())
	}

	void onReceive(msg) {
		println "*** receive: ${msg}"
	}
}

def system = ActorSystem.create('sample')

system.actorOf(Props.create(SampleListener))

System.in.read()

Await.ready(system.terminate(), Duration.create(5, TimeUnit.SECONDS))
