include Java

import org.jbpm.pvm.Activity
import org.jbpm.pvm.Execution
import org.jbpm.pvm.ProcessDefinition
import org.jbpm.pvm.ProcessFactory

def createActivity(name)
	#Activity インターフェースの execute メソッド実装オブジェクト生成
	Activity.impl(:execute) do |method, exec|
		puts "#{name} : node name: #{exec.node.name} - call #{method}"
	end
end

class Simple
	include Activity

	def initialize(name)
		@name = name
	end

	def execute(exec)
		puts "#{@name} : node name: #{exec.node.name}"
	end
end

pd = ProcessFactory.build.
	node("first").initial.behaviour(createActivity :FirstAct).
		transition.to("second").
	node("second").behaviour(createActivity :SecondAct).
		transition.to("third").
	node("third").behaviour(Simple.new(:ThirdAct)).
done

exec = pd.startExecution

puts "node name = #{exec.node.name}, ended=#{exec.ended}, active=#{exec.active}, finished=#{exec.finished}"