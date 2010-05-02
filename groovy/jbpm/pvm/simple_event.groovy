
import org.jbpm.pvm.Activity
import org.jbpm.pvm.Execution
import org.jbpm.pvm.ProcessDefinition
import org.jbpm.pvm.ProcessFactory

class Sample implements Activity {

	def name

	public void execute(Execution exec) {
		println "$name: name=${exec.name}, key=${exec.key}, eventSource=${exec.eventSource}, event=${exec.event}, exception=${exec.exception}"
	}
}

pd = ProcessFactory.build()
	.node("first").initial().behaviour(new Sample(name: "FirstState"))
		.event("node-leave").listener(new Sample(name: "node-leave"))
		.transition().to("second")
	.node("second").behaviour(new Sample(name: "SecondState"))
.done()

exec = pd.startExecution()

println "${exec.node.name} : ended=${exec.ended}, active=${exec.active}, finished=${exec.finished}"
