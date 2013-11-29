@Grab('net.dempsy:lib-dempsyimpl:0.7.9')
@Grab('org.slf4j:slf4j-log4j12:1.7.5')
import com.nokia.dempsy.annotations.Activation
import com.nokia.dempsy.annotations.MessageKey
import com.nokia.dempsy.annotations.MessageProcessor
import com.nokia.dempsy.annotations.MessageHandler
import com.nokia.dempsy.annotations.Output

import com.nokia.dempsy.Adaptor
import com.nokia.dempsy.Dempsy
import com.nokia.dempsy.Dispatcher

import com.nokia.dempsy.config.ApplicationDefinition
import com.nokia.dempsy.config.ClusterDefinition

import com.nokia.dempsy.output.OutputExecuter

import com.nokia.dempsy.cluster.invm.LocalClusterSessionFactory
import com.nokia.dempsy.router.AlwaysInCurrentCluster
import com.nokia.dempsy.router.DecentralizedRoutingStrategy
import com.nokia.dempsy.serialization.kryo.KryoSerializer
import com.nokia.dempsy.monitoring.coda.StatsCollectorFactoryCoda
import com.nokia.dempsy.messagetransport.blockingqueue.BlockingQueueTransport

import groovy.transform.*

// (1) メッセージクラスの作成
@TupleConstructor
class Money {
	String moneyText

	@MessageKey
	String getMoneyText() {
		moneyText
	}
}

// (2) メッセージ処理クラスの作成
@MessageProcessor
@AutoClone
class MoneyCount {
	private long count = 0
	private String key

	@Activation
	void setMoneyKey(String key) {
		println 'MoneyCount.activation'
		this.key = key
	}

	@MessageHandler
	void countMoney(Money money) {
		count++
	}

	@Output
	void printResults() {
		println "key: ${key}, count: ${count}"
	}
}

// (3) アダプタークラスの作成
class MoneyAdaptor implements Adaptor {
	Dispatcher dispatcher
	private File inputFile

	MoneyAdaptor(File inputFile) {
		this.inputFile = inputFile
	}

	void start() {
		println 'MoneyAdaptor.start ...'

		inputFile.eachLine { line ->
			dispatcher.dispatch(new Money(line))
		}
	}

	void stop() {
		println 'MoneyAdaptor.stop'
	}
}

// (4) アプリケーション定義の構築
def mp = new ClusterDefinition('mp', new MoneyCount())
// 終了時に結果を出力
mp.outputExecuter = [
	setOutputInvoker: { invoker ->
		println 'OutputExecuter.setOutputInvoker'
		this.invoker = invoker
	},
	start: { ->
		println 'OutputExecuter.start'
	},
	stop: { ->
		println 'OutputExecuter.stop'
		// 処理結果を出力
		invoker.invokeOutput()
	}
] as OutputExecuter

def app = new ApplicationDefinition('money-count').add(
	new ClusterDefinition('adaptor', new MoneyAdaptor(new File(args[0]))),
	mp
)

// (5) Dempsy の構築と実行
def dempsy = new Dempsy()

dempsy.applicationDefinitions = [app]
dempsy.clusterSessionFactory = new LocalClusterSessionFactory()
dempsy.clusterCheck = new AlwaysInCurrentCluster()
dempsy.defaultRoutingStrategy = new DecentralizedRoutingStrategy(1, 1)
dempsy.defaultSerializer = new KryoSerializer()
dempsy.defaultStatsCollectorFactory = new StatsCollectorFactoryCoda()
dempsy.defaultTransport = new BlockingQueueTransport()

dempsy.start()

Runtime.runtime.addShutdownHook { ->
	println 'shutdown ...'
	dempsy.stop()
}
