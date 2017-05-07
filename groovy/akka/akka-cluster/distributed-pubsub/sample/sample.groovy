@Grab('com.typesafe.akka:akka-cluster-tools_2.12:2.5.0')
import akka.actor.UntypedActor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props

import akka.cluster.Cluster
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class SampleSubscriber extends UntypedActor {

	SampleSubscriber() {
		def mediator = DistributedPubSub.get(getContext().system()).mediator()

		mediator.tell(
			new DistributedPubSubMediator.Subscribe('topic1', getSelf()),
			getSelf()
		)

		println "*** new Subscriber< ${getSelf()} >"
	}

	void onReceive(msg) {
		println "*** Subscriber receive< ${getSelf()} >: ${msg}"
	}
}

class SamplePublisher extends UntypedActor {
	def mediator = DistributedPubSub.get(getContext().system()).mediator()

	void onReceive(msg) {
		if (msg instanceof String) {
			println "*** Publisher receive < ${getSelf()} >: ${msg}"

			mediator.tell(
				new DistributedPubSubMediator.Publish('topic1', "${msg} !!!".toString()),
				getSelf()
			)
		}
		else {
			unhandled(msg)
		}
	}
}

def systemName = 'sample1'

def sys1 = ActorSystem.create(systemName)

def joinAddr = Cluster.get(sys1).selfAddress()

Cluster.get(sys1).join(joinAddr)
sys1.actorOf(Props.create(SampleSubscriber), 'sub1')

def sys2 = ActorSystem.create(systemName)
Cluster.get(sys2).join(joinAddr)
sys2.actorOf(Props.create(SampleSubscriber), 'sub2')

def sys3 = ActorSystem.create(systemName)
Cluster.get(sys3).join(joinAddr)
sys3.actorOf(Props.create(SampleSubscriber), 'sub3')

Thread.sleep(3000)

def publisher = sys1.actorOf(Props.create(SamplePublisher), 'pub1')

(0..<10).each {
	publisher.tell(it.toString(), ActorRef.noSender())
}

System.in.read()

Await.ready(
	sys1.terminate()
		.completeWith(sys2.terminate())
		.completeWith(sys3.terminate())
	,
	Duration.create(5, TimeUnit.SECONDS)
)
