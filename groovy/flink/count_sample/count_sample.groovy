@Grapes([
	@Grab('org.apache.flink:flink-java:1.2.0'),
	@GrabExclude('io.netty#netty;3.7.0.Final')
])
@Grab('org.apache.flink:flink-clients_2.10:1.2.0')
@Grab('org.jboss.netty:netty:3.2.10.Final')
import org.apache.flink.api.java.ExecutionEnvironment
import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.api.java.tuple.Tuple2
import org.apache.flink.util.Collector
import groovy.transform.CompileStatic

def env = ExecutionEnvironment.createLocalEnvironment()

def input = env.fromElements(
	'1 10 5 50 100 1 10',
	'100 1 1 10 5 100 '
)

@CompileStatic
class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
	void flatMap(String v, Collector out) {
		v.split('\\s').each { w ->
			out.collect(new Tuple2(w, 1))
		}
	}
}

def res = input.flatMap(new Splitter()).groupBy(0).sum(1)

res.print()
