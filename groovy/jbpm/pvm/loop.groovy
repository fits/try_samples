
import org.jbpm.pvm.Activity
import org.jbpm.pvm.ExternalActivity
import org.jbpm.pvm.Execution
import org.jbpm.pvm.ProcessDefinition
import org.jbpm.pvm.ProcessFactory
import org.jbpm.pvm.SignalDefinition

class NullActivity implements Activity {
	void execute(Execution exec) {
	}
}

class LoopCounter implements ExternalActivity {

	def loopCount

	void execute(Execution exec) {
		exec.waitForSignal();
	}

	void signal(Execution exec, String signal, Map<String, Object> params) {
		def count = (exec.hasVariable("count"))? exec.getVariable("count"): 0;
		count++
		exec.setVariable("count", count)

		if (count < loopCount) {
			//continue に移行
			exec.take("continue")
		}
		else {
			//next に移行
			exec.take("next")
		}
	}

	Set<SignalDefinition> getSignals(Execution exec) {
	}
}

printExec = {
	println "${exec.node.name} : ended=${exec.ended}, active=${exec.active}, finished=${exec.finished}"
}

//プロセス定義
pd = ProcessFactory.build()
	.node("1st").initial().behaviour(new LoopCounter(loopCount: 3))
		.transition("continue").to("1st")
		.transition("next").to("2nd")
	.node("2nd").behaviour(new NullActivity())
.done()

exec = pd.startExecution()

while(exec.active) {
	printExec()
	exec.signal()
}

printExec()
