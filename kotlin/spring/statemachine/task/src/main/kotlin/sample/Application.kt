package sample

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration
import org.springframework.statemachine.config.StateMachineFactory
import org.springframework.statemachine.listener.StateMachineListenerAdapter
import org.springframework.statemachine.state.State
import sample.fsm.*

@SpringBootApplication
@Configuration
class Application : CommandLineRunner {
    @Autowired
    @Qualifier("taskFactory")
    lateinit var taskFactory: StateMachineFactory<OrderStates, OrderEvents>

    override fun run(vararg args: String?) {
        val task = taskFactory.stateMachine
        task.addStateListener(TaskOrderListener())
        task.start()

        task.sendEvent(OrderEvents.OrderTask)
        task.sendEvent(OrderEvents.SelectWorker)
        task.sendEvent(OrderEvents.ReportTask)

        Thread.sleep(7000)

        task.sendEvent(OrderEvents.OrderTask)
        task.sendEvent(OrderEvents.SelectWorker)

        Thread.sleep(7000)

        task.sendEvent(OrderEvents.OrderTask)
        task.sendEvent(OrderEvents.CancelTask)


    }
}

class TaskOrderListener : StateMachineListenerAdapter<OrderStates, OrderEvents>() {
    override fun stateChanged(from: State<OrderStates, OrderEvents>?, to: State<OrderStates, OrderEvents>?) {
        println("*** taskOrder state changed: ${from?.id} -> ${to?.id}")
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
