
import java.util.*

import org.jbpm.pvm.ExternalActivity
import org.jbpm.pvm.Execution
import org.jbpm.pvm.ProcessDefinition
import org.jbpm.pvm.ProcessFactory
import org.jbpm.pvm.SignalDefinition

class Sample implements ExternalActivity {

	def name

	void execute(Execution exec) {
		exec.waitForSignal();
	}

	void signal(Execution exec, String signal, Map<String, Object> params) {
		println "call signal : ${signal}"
	//	exec.take(signal)
	}

	Set<SignalDefinition> getSignals(Execution exec) {
		println "call getSignals"
	}
}

pd = ProcessFactory.build()
	.node("test1").initial().behaviour(new Sample(name: "tester"))
		.transition().to("test2")
	.node("test2").behaviour(new Sample(name: "aaaaa"))
.done()

exec = pd.startExecution()

printExec = {
	println "${exec.node.name} : ended=${exec.ended}, active=${exec.active}, finished=${exec.finished}"
}

printExec()

exec.signal("paint")

printExec()

exec.signal("paint2")

printExec()
