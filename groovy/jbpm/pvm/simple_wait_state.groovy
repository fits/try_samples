
import java.util.*

import org.jbpm.pvm.Activity
import org.jbpm.pvm.ExternalActivity
import org.jbpm.pvm.Execution
import org.jbpm.pvm.ProcessDefinition
import org.jbpm.pvm.ProcessFactory
import org.jbpm.pvm.SignalDefinition

class Sample implements Activity {

	def name

	void execute(Execution exec) {
		println "$name - execute"
	}
}

class WaitSample implements ExternalActivity {

	def name

	void execute(Execution exec) {
		println "$name - execute"
		exec.waitForSignal();
	}

	void signal(Execution exec, String signal, Map<String, Object> params) {
		println "$name - signal : ${signal}"

		if (signal != null) {
			exec.take(signal)
		}
	}

	Set<SignalDefinition> getSignals(Execution exec) {
		println "call getSignals"
	}
}

pd = ProcessFactory.build()
	.node("first").initial().behaviour(new Sample(name: "FirstState"))
		.transition().to("second")
	.node("second").behaviour(new WaitSample(name: "SecondState"))
		.transition("success").to("third")
		.transition("fail").to("end")
	.node("third").behaviour(new Sample(name: "ThirdState"))
		.transition().to("end")
	.node("end").behaviour(new WaitSample(name: "EndState"))
.done()

exec = pd.startExecution()

printExec = {
	println "${exec.node.name} : ended=${exec.ended}, active=${exec.active}, finished=${exec.finished}"
}

printExec()

exec.signal("success")

printExec()

exec.signal()

printExec()
