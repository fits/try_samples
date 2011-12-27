@Grapes([
	@GrabResolver(name = "clojars.org", root = "http://clojars.org/repo"),
	@Grab("storm:storm:0.6.1-rc")
])
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

//入力データ
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

	void nextTuple() {
		if (dataSet.hasNext()) {
			//標準入力の 1行分をデータとして設定
			collector.emit(new Values(dataSet.next()))
		}
	}

	void declareOutputFields(OutputFieldsDeclarer decl) {
		decl.declare(new Fields("money"))
	}

	void close() {}
	void ack(msgId) {}
	void fail(msgId) {}
}
//カウント処理
class CountBolt implements IBasicBolt {
	def counter = [:]

	void execute(Tuple input, BasicOutputCollector collector) {
		def money = input.getValueByField("money")
		def count = (counter.containsKey(money))? counter.get(money): 0

		count++
		counter.put(money, count)

		collector.emit(new Values(money, count))
	}

	void declareOutputFields(OutputFieldsDeclarer decl) {
		decl.declare(new Fields("money", "count"))
	}

	void prepare(Map conf, TopologyContext context) {}
	void cleanup() {}
}
//出力処理
class PrintBolt implements IBasicBolt {
	void execute(Tuple input, BasicOutputCollector collector) {
		def money = input.getValueByField("money")
		def count = input.getValueByField("count")

		println "${money} = ${count}"
	}

	void prepare(Map conf, TopologyContext context) {}
	void cleanup() {}
	void declareOutputFields(OutputFieldsDeclarer decl) {}
}


def builder = new TopologyBuilder()

builder.setSpout("sp1", new StdInSpout())
//Bolt はシリアライズされて、各ワーカーに渡されるため、
//fieldsGrouping で同一の money を同じワーカーで処理するよう指定する
builder.setBolt("bo1", new CountBolt(), 4).fieldsGrouping("sp1", new Fields("money"))
builder.setBolt("bo2", new PrintBolt(), 4).shuffleGrouping("bo1")

def conf = new Config()
conf.debug = false
//ローカルモード用の Cluster
def cluster = new LocalCluster()
//Topology を設定
cluster.submitTopology("moneycount", conf, builder.createTopology())

//Topology は終了しないため 5秒待った後でシャットダウン開始
Utils.sleep(5000)

//Topology の削除
cluster.killTopology("moneycount")

//ログファイルの削除に失敗して IOException を throw するので
//try-catch している
try {
	//シャットダウン
	cluster.shutdown()
} catch (e) {
	println e
}
