package sample.fsm

import org.springframework.context.annotation.Configuration
import org.springframework.statemachine.StateContext
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer

enum class OrderStates { Idle, Ordering, Waiting, Complete, Cancel }
enum class OrderEvents { OrderTask, SelectWorker, NoWorker, ReportTask, CancelTask }

@Configuration
@EnableStateMachineFactory(name = arrayOf("taskFactory"), contextEvents = false)
class TaskOrderStateMachine : EnumStateMachineConfigurerAdapter<OrderStates, OrderEvents>() {
    override fun configure(states: StateMachineStateConfigurer<OrderStates, OrderEvents>?) {
        super.configure(states)

        states!!.withStates()
                .initial(OrderStates.Idle)
                .states(OrderStates.values().toSet())
    }

    override fun configure(transitions: StateMachineTransitionConfigurer<OrderStates, OrderEvents>?) {
        super.configure(transitions)

        transitions!!
                .withExternal()
                    .source(OrderStates.Idle)
                    .target(OrderStates.Ordering)
                    .event(OrderEvents.OrderTask)
                .and()
                .withExternal()
                    .source(OrderStates.Ordering)
                    .target(OrderStates.Waiting)
                    .event(OrderEvents.SelectWorker)
                .and()
                .withExternal()
                    .source(OrderStates.Ordering)
                    .target(OrderStates.Cancel)
                    .event(OrderEvents.NoWorker)
                .and()
                .withExternal()
                    .source(OrderStates.Waiting)
                    .target(OrderStates.Complete)
                    .event(OrderEvents.ReportTask)
                .and()
                .withExternal()
                    .source(OrderStates.Ordering)
                    .target(OrderStates.Cancel)
                    .event(OrderEvents.CancelTask)
                .and()
                .withExternal()
                    .source(OrderStates.Waiting)
                    .target(OrderStates.Cancel)
                    .event(OrderEvents.CancelTask)
                .and()
                .withExternal()
                    .source(OrderStates.Complete)
                    .target(OrderStates.Idle)
                    .timerOnce(6000)
                .and()
                .withExternal()
                    .source(OrderStates.Cancel)
                    .target(OrderStates.Idle)
                    .timerOnce(3000)
                .and()
                .withInternal()
                    .source(OrderStates.Waiting)
                    .action(this::timeout)
                    .timerOnce(3000)
    }

    private fun timeout(ctx: StateContext<OrderStates, OrderEvents>) {
        println("TIMEOUT: ${ctx}")

        ctx.stateMachine.sendEvent(OrderEvents.CancelTask)
    }
}