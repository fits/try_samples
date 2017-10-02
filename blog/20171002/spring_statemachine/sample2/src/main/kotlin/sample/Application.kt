package sample

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.messaging.Message
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.statemachine.listener.StateMachineListenerAdapter
import org.springframework.statemachine.state.State

@SpringBootApplication
class Application : CommandLineRunner {
    @Autowired
    lateinit var stateMachineFactory: StateMachineFactory<States, Events>

    override fun run(vararg args: String?) {
        val machine = stateMachineFactory.stateMachine
        machine.addStateListener(SampleListener())

        machine.sendEvent(Events.On)
        machine.sendEvent(Events.Off)

        machine.sendEvent(Events.Off)

        machine.sendEvent(Events.On)
    }
}

class SampleListener : StateMachineListenerAdapter<States, Events>() {
    override fun stateChanged(from: State<States, Events>?, to: State<States, Events>?) {
        println("*** stateChanged: ${from?.id} -> ${to?.id}")
    }

    override fun eventNotAccepted(event: Message<Events>?) {
        println("*** eventNotAccepted: ${event?.payload}")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
