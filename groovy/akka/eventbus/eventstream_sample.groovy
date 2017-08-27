@Grab('com.typesafe.akka:akka-actor_2.12:2.5.4')
import akka.actor.*

class SampleActor extends AbstractActor {
	def name

	SampleActor(name) {
		this.name = name
	}

	AbstractActor.Receive createReceive() {
		receiveBuilder()
			.match(String) { msg ->
				println "*** receive: ${msg}, ${name}"
			}
			.build()
	}
}

def system = ActorSystem.create()

def actor1 = system.actorOf(Props.create(SampleActor, 'one'))
def actor2 = system.actorOf(Props.create(SampleActor, 'two'))

system.eventStream().subscribe(actor1, String)
system.eventStream().subscribe(actor2, String)

system.eventStream().publish('message1')
system.eventStream().publish('message2')
system.eventStream().publish('message3')

sleep 1000

system.terminate()
