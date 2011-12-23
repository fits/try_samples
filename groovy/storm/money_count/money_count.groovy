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
			collector.emit(new Values(dataSet.next(), 1))
		}
	}
	void ack(msgId) {}
	void fail(msgId) {}

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

builder.setSpout("sp1", new StdInSpout())
builder.setBolt("bo1", new PrintBolt()).fieldsGrouping("sp1", new Fields("money"))

def runner = new LocalCluster()
runner.submitTopology("moneycount", new Config(), builder.createTopology())

Utils.sleep(5000)
runner.shutdown()

