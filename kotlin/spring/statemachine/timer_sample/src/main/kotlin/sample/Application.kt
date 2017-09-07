package sample

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer
import org.springframework.statemachine.listener.StateMachineListenerAdapter
import org.springframework.statemachine.state.State

enum class States { Idle, Active }
enum class Events { On, Off }

@SpringBootApplication
class Application : CommandLineRunner {
    @Autowired
    lateinit var factory: StateMachineFactory<States, Events>

    override fun run(vararg args: String?) {
        val stateMachine = factory.stateMachine

        stateMachine.addStateListener(SampleListener())
        stateMachine.start()

        stateMachine.sendEvent(Events.On)
        stateMachine.sendEvent(Events.Off)

        stateMachine.sendEvent(Events.On)

        Thread.sleep(3000)
        println("* send off")

        stateMachine.sendEvent(Events.Off)
    }
}

@Configuration
@EnableStateMachineFactory(contextEvents = false)
class SampleConfigurer : EnumStateMachineConfigurerAdapter<States, Events>() {

    override fun configure(states: StateMachineStateConfigurer<States, Events>?) {
        states!!.withStates()
                .initial(States.Idle)
                .states(States.values().toSet())
    }

    override fun configure(transitions: StateMachineTransitionConfigurer<States, Events>?) {
        transitions!!
                .withExternal().source(States.Idle).target(States.Active).event(Events.On)
                .and()
                .withExternal().source(States.Active).target(States.Idle).event(Events.Off)
                .and()
                // timeout
                .withInternal().source(States.Active).timerOnce(2000).action {
                    println("** TIMEOUT")
                    it.stateMachine.sendEvent(Events.Off)
                }
    }
}

class SampleListener : StateMachineListenerAdapter<States, Events>() {
    override fun stateChanged(from: State<States, Events>?, to: State<States, Events>?) {
        println("*** state changed ${from?.id} -> ${to?.id}")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
