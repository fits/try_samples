@Grapes([
	@GrabResolver(name = "clojars.org", root = "http://clojars.org/repo"),
	@Grab("storm:storm:0.6.1-rc")
])
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import backtype.storm.Config
import backtype.storm.LocalCluster
import backtype.storm.spout.SpoutOutputCollector
import backtype.storm.task.TopologyContext
import backtype.storm.topology.IRichSpout
import backtype.storm.topology.IBasicBolt
import backtype.storm.topology.OutputFieldsDeclarer
import backtype.storm.topology.BasicOutputCollector
import backtype.storm.topology.TopologyBuilder
import backtype.storm.tuple.Fields
import backtype.storm.tuple.Tuple
import backtype.storm.tuple.Values
import backtype.storm.utils.Utils
import backtype.storm.utils.RegisteredGlobalState

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

class StateCountBolt implements IBasicBolt {
	def trackId

	StateCountBolt(trackId) {
		this.trackId = trackId
	}

	void prepare(Map conf, TopologyContext context) {}

	void execute(Tuple input, BasicOutputCollector collector) {
		def money = input.getValueByField("money")

		def stats = RegisteredGlobalState.getState(trackId)

		//未登録の場合にカウントを登録
		stats.putIfAbsent(money, new AtomicInteger())

		//カウントアップしてカウントを取得
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

builder.setSpout("sp1", new StdInSpout())
//ConcurrentHashMap であれば fieldsGrouping は不要
builder.setBolt("bo1", new StateCountBolt(trackId), 4).shuffleGrouping("sp1")

def conf = new Config()

def cluster = new LocalCluster()
cluster.submitTopology("moneycount2", conf, builder.createTopology())

Utils.sleep(5000)

cluster.killTopology("moneycount2")

try {
	cluster.shutdown()
} catch (e) {
	println e
}

//結果出力
RegisteredGlobalState.getState(trackId).each {k, v ->
	println "${k} = ${v}"
}
