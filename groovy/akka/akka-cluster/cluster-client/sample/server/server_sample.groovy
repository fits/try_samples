@Grab('com.typesafe.akka:akka-cluster-tools_2.12:2.5.0-RC1')
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.UntypedActor

import akka.cluster.Cluster
import akka.cluster.client.ClusterClient
import akka.cluster.client.ClusterClientReceptionist
import akka.cluster.client.ClusterClientSettings

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class ASample extends UntypedActor {
	void onReceive(msg) {
		println "*** A-Sample receive< ${getSelf()} >: ${msg}"
	}
}

class BSample extends UntypedActor {
	void onReceive(msg) {
		println "*** B-Sample receive< ${getSelf()} >: ${msg}"
	}
}

def system = ActorSystem.create('sample')

def a = system.actorOf(Props.create(ASample), 'a-sample')
ClusterClientReceptionist.get(system).registerService(a)

def b = system.actorOf(Props.create(BSample), 'b-sample')
ClusterClientReceptionist.get(system).registerService(b)

System.in.read()

Await.ready(system.terminate(), Duration.create(5, TimeUnit.SECONDS))
