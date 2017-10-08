@Grab('com.typesafe.akka:akka-actor_2.12:2.5.6')
import akka.actor.AbstractFSM
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props

import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

enum States { Idle, Active }
enum Events { On, Off }

class SampleStateMachine extends AbstractFSM<States, Integer> {
	{
		startWith(States.Idle, 0)

		when(States.Idle, matchEventEquals(Events.On) { event, data ->
			goTo(States.Active).using(data + 1)
		})

		// no message timeout
		when(States.Active, Duration.create(2, TimeUnit.SECONDS), 
			matchEventEquals(Events.Off) { event, data ->
				goTo(States.Idle)
			}.eventEquals(StateTimeout()) { event, data ->
				println "*** timeout: event=${event}, data=${data}"

				goTo(States.Idle)
			/*
				self().tell(Events.Off, self())
				stay()
			*/
			}
		)

		whenUnhandled(
			matchAnyEvent { event, data ->
				println "*** Unhandled event=${event}, data=${data}"
				stay()
			}
		)

		onTransition { from, to -> 
			println "*** stateChanged: ${from} -> ${to}, data=${stateData()}, next data=${nextStateData()}"
		}

		initialize()
	}
}

def system = ActorSystem.create()

def actor = system.actorOf(Props.create(SampleStateMachine))

actor.tell(Events.On, ActorRef.noSender())
actor.tell(Events.Off, ActorRef.noSender())

actor.tell(Events.Off, ActorRef.noSender())

actor.tell(Events.On, ActorRef.noSender())

sleep 1500

// reset timeout
actor.tell('invalid-message', ActorRef.noSender())

sleep 1500

// reset timeout
actor.tell('invalid-message', ActorRef.noSender())

sleep 2500

system.terminate()
