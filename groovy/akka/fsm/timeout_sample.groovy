@Grab('com.typesafe.akka:akka-actor_2.12:2.5.4')
import akka.actor.*

import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

import groovy.transform.*

enum WorkerState { Idle, Pending, Working, Complete }

interface WorkerData {}

@Immutable
class NoneTask implements WorkerData {
}

@Immutable
class Task implements WorkerData {
	String name
}

@Immutable
class TaskResult implements WorkerData {
	Task task
	TaskReport result
}

@Immutable
class TaskReport {
	String report
}

class Worker extends AbstractFSM<WorkerState, WorkerData> {
	{
		def noneTask = new NoneTask()

		startWith(WorkerState.Idle, noneTask)

		when(WorkerState.Idle, matchEvent(Task) { event, data ->
			println "*** Idle ${event}, ${data}"
			goTo(WorkerState.Pending).using(event)
		})

		when(WorkerState.Pending, matchEvent(Boolean) { event, data ->
			println "*** Pending ${event}, ${data}"

			event ? goTo(WorkerState.Working) : goTo(WorkerState.Idle).using(noneTask)
		})

		when(WorkerState.Working, matchEvent(TaskReport) { event, data ->
			println "*** Working ${event}, ${data}"

			goTo(WorkerState.Complete).using(new TaskResult(data, event))
		})

		when(WorkerState.Complete, Duration.create(3, TimeUnit.SECONDS), 
			matchEventEquals(StateTimeout()) { event, data ->
				println "*** Complete ${event}, ${data}"

				goTo(WorkerState.Idle).using(noneTask)
			}
		)

		onTransition { s1, s2 ->
			println "--- state change: ${s1} -> ${s2}, data: ${stateData()}, nextData: ${nextStateData()}"
		}

		initialize()

	}
}

def system = ActorSystem.create()

def w1 = system.actorOf(Props.create(Worker))

w1.tell(new Task('work0'), null)
w1.tell(false, null)

w1.tell(new Task('work1'), null)
w1.tell(true, null)
w1.tell(new TaskReport("done"), null)

sleep 5000

system.terminate()
