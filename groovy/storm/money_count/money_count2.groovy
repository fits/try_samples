@Grapes([
	@GrabResolver(name = "clojars.org", root = "http://clojars.org/repo"),
	@Grab("storm:storm:0.6.1-rc")
])
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
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
	def trackId

	CountBolt(trackId) {
		this.trackId = trackId
	}

	void prepare(Map conf, TopologyContext context) {}

	void execute(Tuple input, BasicOutputCollector collector) {
		def money = input.getValueByField("money")

		def stats = RegisteredGlobalState.getState(trackId)

		stats.putIfAbsent(money, new AtomicInteger())

		int count = stats.get(money).incrementAndGet()
		collector.emit(new Values(money, count))
	}

	void cleanup() {}

	void declareOutputFields(OutputFieldsDeclarer decl) {
		decl.declare(new Fields("money", "count"))
	}
}


def trackId = RegisteredGlobalState.registerState(new ConcurrentHashMap())

def builder = new TopologyBuilder()

builder.setSpout("sp1", new StdInSpout(), 4)
//ConcurrentHashMap を使えば fieldsGrouping でなくてもよい
builder.setBolt("bo1", new CountBolt(trackId), 4).shuffleGrouping("sp1")

def conf = new Config()

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

//結果出力
RegisteredGlobalState.getState(trackId).each {k, v ->
	println "${k} = ${v}"
}
