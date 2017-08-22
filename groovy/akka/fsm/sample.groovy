@Grab('com.typesafe.akka:akka-actor_2.12:2.5.4')
import akka.actor.*
import groovy.transform.*

enum St { Idle, Active }

interface Data {}

@Immutable
class SampleData implements Data {
	int value
}

class Worker extends AbstractFSM<St, Data> {
	{
		startWith(St.Idle, new SampleData(0))

		when(St.Idle, matchEvent(Integer) { event, data ->
			println "**** Idle event : ${event}, data: ${data}"

			(event > 0) ?
				goTo(St.Active).using(new SampleData(event)) : 
				stay().using(new SampleData(event))
		})

		when(St.Active, matchEvent(String) { event, data ->
			println "**** Active event : ${event}, data: ${data}"

			goTo(St.Idle).using(new SampleData(0))
		})

		whenUnhandled(
			matchAnyEvent { event, data ->
				println "**** Unhandled event : ${event}, data: ${data}"

				goTo(St.Idle).using(new SampleData(0))
			}
		)

		initialize()
	}
}

def system = ActorSystem.create()

def actor = system.actorOf(Props.create(Worker))

actor.tell(0, null)
actor.tell(1, null)

actor.tell('end', null)

actor.tell(-1, null)
actor.tell('invalid', null)

sleep 1000

system.terminate()
