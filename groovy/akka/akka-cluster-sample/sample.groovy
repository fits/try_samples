@Grab('com.typesafe.akka:akka-cluster_2.12:2.5-M1')
import akka.actor.*
import akka.cluster.*
import akka.cluster.singleton.*

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class SampleActor extends UntypedActor {
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

		cluster.unsubscribe(getSelf())
	}

	void onReceive(msg) {
		println "*** receive: ${msg}"
	}
}

def system = ActorSystem.create('sample')

system.actorOf(Props.create(SampleActor))

System.in.read()

Await.result(system.terminate(), Duration.create(5, TimeUnit.SECONDS))
