@Grapes([
	@Grab('org.apache.flink:flink-java:1.2.0'),
	@GrabExclude('io.netty#netty;3.7.0.Final')
])
@Grab('org.apache.flink:flink-clients_2.10:1.2.0')
@Grab('org.jboss.netty:netty:3.2.10.Final')
import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.common.functions.GroupReduceFunction
import org.apache.flink.api.java.ExecutionEnvironment
import org.apache.flink.api.java.tuple.Tuple2
import org.apache.flink.util.Collector

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@Canonical
class Word {
	String word
	int count
}

@CompileStatic
class ToWord implements MapFunction<String, Word> {
	Word map(String v) {
		new Word(v, 1)
	}
}

@CompileStatic
class Sum implements GroupReduceFunction<Word, Word> {
	void reduce(Iterable<Word> v, Collector<Word> out) {
		def vlist = v.asList()
		out.collect(new Word(vlist.first().word, vlist*.count.sum() as int))
	}
}

def env = ExecutionEnvironment.createLocalEnvironment()

def res = env.readTextFile(args[0])
				.map(new ToWord())
				.groupBy('word')
				.reduceGroup(new Sum())

res.print()
