package sample

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.messaging.Message
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.config.StateMachineBuilder
import org.springframework.statemachine.listener.StateMachineListenerAdapter
import org.springframework.statemachine.state.State

enum class States { Idle, Active }
enum class Events { On, Off }

@SpringBootApplication
class Application : CommandLineRunner {
    override fun run(vararg args: String?) {
        val machine = stateMachine()
        machine.addStateListener(SampleListener())

        machine.start()

        machine.sendEvent(Events.On)
        machine.sendEvent(Events.Off)

        machine.sendEvent(Events.Off)
    }

    @Bean
    fun stateMachine(): StateMachine<States, Events> {
        val builder = StateMachineBuilder.builder<States, Events>()

        builder.configureStates().withStates()
                .initial(States.Idle).states(States.values().toSet())

        builder.configureTransitions()
                .withExternal().source(States.Idle).target(States.Active).event(Events.On)
                .and()
                .withExternal().source(States.Active).target(States.Idle).event(Events.Off)

        return builder.build()
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
