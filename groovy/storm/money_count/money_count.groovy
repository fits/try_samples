@Grapes([
	@GrabResolver(name = "clojars.org", root = "http://clojars.org/repo"),
	@Grab("storm:storm:0.6.1-rc")
])

import backtype.storm.*
import backtype.storm.spout.*
import backtype.storm.task.*
import backtype.storm.topology.*
import backtype.storm.tuple.*
import backtype.storm.utils.*

class StdInSpout implements IRichSpout {
	def collector
	def dataSet

	boolean isDistributed() {
		false
	}

	void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector
		dataSet = System.in.newReader().iterator()
	}

	void close() {}

	void nextTuple() {
		if (dataSet.hasNext()) {
			collector.emit(new Values(dataSet.next()))
		}
	}

	void ack(msgId) {}

	void fail(msgId) {}

	void declareOutputFields(OutputFieldsDeclarer decl) {
		decl.declare(new Fields("money"))
	}
}

class CountBolt implements IBasicBolt {
	def counter = [:]

	void prepare(Map conf, TopologyContext context) {}

	void execute(Tuple input, BasicOutputCollector collector) {
		def money = input.getValueByField("money")
		def count = (counter.containsKey(money))? counter.get(money): 0

		count++
		counter.put(money, count)

		collector.emit(new Values(money, count))
	}

	void cleanup() {}

	void declareOutputFields(OutputFieldsDeclarer decl) {
		decl.declare(new Fields("money", "count"))
	}
}

class PrintBolt implements IBasicBolt {
	void prepare(Map conf, TopologyContext context) {}
	void execute(Tuple input, BasicOutputCollector collector) {
		println(input)
	}
	void cleanup() {}
	void declareOutputFields(OutputFieldsDeclarer decl) {}
}


def builder = new TopologyBuilder()

builder.setSpout("sp1", new StdInSpout(), 4)
//Bolt はシリアライズされて、各ワーカーに渡されるため、
//fieldsGrouping で同一の money を同じワーカーで処理するよう指定する
builder.setBolt("bo1", new CountBolt(), 4).fieldsGrouping("sp1", new Fields("money"))
builder.setBolt("bo2", new PrintBolt(), 4).shuffleGrouping("bo1")

def conf = new Config()
conf.debug = false

def cluster = new LocalCluster()
cluster.submitTopology("moneycount", conf, builder.createTopology())

Utils.sleep(5000)

//Topology は終了しないため（動作し続けることが前提）
//シャットダウンを実施
cluster.killTopology("moneycount")

try {
	cluster.shutdown()
} catch (e) {
	println e
}
