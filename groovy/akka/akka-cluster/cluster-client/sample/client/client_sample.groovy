@Grab('com.typesafe.akka:akka-cluster-tools_2.12:2.5.0-RC1')
import akka.actor.ActorSystem
import akka.actor.ActorPaths

import akka.cluster.client.ClusterClient
import akka.cluster.client.ClusterClientSettings

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

def system = ActorSystem.create()

def contacts = [
	ActorPaths.fromString(
		'akka.tcp://sample@127.0.0.1:2051/system/receptionist'
	)
] as Set

def c = system.actorOf(
	ClusterClient.props(
		ClusterClientSettings.create(system).withInitialContacts(contacts)
	),
	'client'
)

c.tell(new ClusterClient.Send('/user/a-sample', 'a'), null)
c.tell(new ClusterClient.Send('/user/b-sample', 'bb'), null)

c.tell(new ClusterClient.SendToAll('/user/a-sample', 'all-a'), null)

System.in.read()

Await.ready(system.terminate(), Duration.create(5, TimeUnit.SECONDS))
