package sample

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.annotation.OnTransition
import org.springframework.statemachine.annotation.WithStateMachine
import org.springframework.statemachine.config.EnableStateMachine
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer

enum class States { Idle, Active }
enum class Events { On, Off }

@SpringBootApplication
class Application : CommandLineRunner {
    @Autowired
    lateinit var stateMachine: StateMachine<States, Events>

    override fun run(vararg args: String?) {
        stateMachine.sendEvent(Events.On)
        stateMachine.sendEvent(Events.Off)

        stateMachine.sendEvent(Events.Off)

        stateMachine.sendEvent(Events.On)
        stateMachine.sendEvent(Events.On)

        stateMachine.sendEvent(Events.Off)
    }
}

@Configuration
@EnableStateMachine(contextEvents = false)
class SampleStateMachine : EnumStateMachineConfigurerAdapter<States, Events>() {
    override fun configure(config: StateMachineConfigurationConfigurer<States, Events>?) {
        config?.let {
            it.withConfiguration()
                    .autoStartup(true)
                    .machineId("monitor")
        }
    }

    override fun configure(states: StateMachineStateConfigurer<States, Events>?) {
        states?.let {
            it.withStates()
                    .initial(States.Idle)
                    .states(States.values().toSet())
        }
    }

    override fun configure(transitions: StateMachineTransitionConfigurer<States, Events>?) {
        transitions?.let {
            it.withExternal().source(States.Idle).target(States.Active).event(Events.On)
                    .and()
                    .withExternal().source(States.Active).target(States.Idle).event(Events.Off)
        }
    }
}

@WithStateMachine(id = "monitor")
class SampleMonitor {
    @OnTransition
    fun transition() {
        println("*** onTransition")
    }

    @OnTransition(source = arrayOf("Idle"), target = arrayOf("Active"))
    fun idleToActive() {
        println("*** onTransition Idle -> Active")
    }

    @OnTransition(source = arrayOf("Active"), target = arrayOf("Idle"))
    fun activeToIdle() {
        println("*** onTransition Active -> Idle")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
