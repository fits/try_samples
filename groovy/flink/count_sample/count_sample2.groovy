@Grapes([
	@Grab('org.apache.flink:flink-java:1.2.0'),
	@GrabExclude('io.netty#netty;3.7.0.Final')
])
@Grab('org.apache.flink:flink-clients_2.10:1.2.0')
@Grab('org.jboss.netty:netty:3.2.10.Final')
import org.apache.flink.api.java.ExecutionEnvironment
import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.api.common.functions.GroupReduceFunction
import org.apache.flink.util.Collector
import groovy.transform.Canonical
import groovy.transform.CompileStatic

def env = ExecutionEnvironment.createLocalEnvironment()

def input = env.fromElements(
	'1 10 5 50 100 1 10',
	'100 1 1 10 5 100 '
)

@Canonical
class Word {
	String word
	int count
}

@CompileStatic
class Splitter implements FlatMapFunction<String, Word> {
	void flatMap(String v, Collector<Word> out) {
		v.split('\\s').each { String w ->
			out.collect(new Word(w, 1))
		}
	}
}

@CompileStatic
class Sum implements GroupReduceFunction<Word, Word> {
	void reduce(Iterable<Word> v, Collector<Word> out) {
		def vlist = v.asList()
		out.collect(new Word(vlist.first().word, vlist*.count.sum() as int))
	}
}

def res = input.flatMap(new Splitter()).groupBy('word').reduceGroup(new Sum())

res.print()
