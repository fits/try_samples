@Grapes([
	@Grab('org.apache.flink:flink-java:1.2.0'),
	@GrabExclude('io.netty#netty;3.7.0.Final')
])
@Grab('org.apache.flink:flink-clients_2.10:1.2.0')
@Grab('org.jboss.netty:netty:3.2.10.Final')
import org.apache.flink.api.java.ExecutionEnvironment
import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.java.tuple.Tuple2
import groovy.transform.CompileStatic

def env = ExecutionEnvironment.createLocalEnvironment()

@CompileStatic
class ToTuple implements MapFunction<String, Tuple2<String, Integer>> {
	Tuple2 map(String v) {
		new Tuple2(v, 1)
	}
}

def res = env.readTextFile(args[0]).map(new ToTuple()).groupBy(0).sum(1)

res.print()
