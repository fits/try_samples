package sample

import org.springframework.statemachine.StateContext
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer

enum class States { Idle, Active }
enum class Events { On, Off }

@EnableStateMachineFactory
class SampleStateMachineConfig : EnumStateMachineConfigurerAdapter<States, Events>() {
    override fun configure(config: StateMachineConfigurationConfigurer<States, Events>?) {
        config!!.withConfiguration()
                .autoStartup(true)
    }

    override fun configure(states: StateMachineStateConfigurer<States, Events>?) {
        states!!.withStates()
                .initial(States.Idle).states(States.values().toSet())
    }

    override fun configure(transitions: StateMachineTransitionConfigurer<States, Events>?) {
        transitions!!
                .withExternal().source(States.Idle).target(States.Active).event(Events.On)
                .and()
                .withExternal().source(States.Active).target(States.Idle).event(Events.Off)
                .and()
                .withInternal().source(States.Active).timerOnce(2000).action(this::timeout)
                //.withExternal().source(States.Active).target(States.Idle).timerOnce(2000)
    }

    private fun timeout(ctx: StateContext<States, Events>) {
        println("*** timeout: ${ctx.source.id}")
        ctx.stateMachine.sendEvent(Events.Off)
    }
}