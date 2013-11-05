@Grab('net.dempsy:lib-dempsyimpl:0.7.9')
@Grab('org.slf4j:slf4j-log4j12:1.7.5')
import com.nokia.dempsy.annotations.*
import com.nokia.dempsy.*
import com.nokia.dempsy.config.*
import com.nokia.dempsy.output.*

import com.nokia.dempsy.cluster.invm.LocalClusterSessionFactory
import com.nokia.dempsy.router.AlwaysInCurrentCluster
import com.nokia.dempsy.router.DecentralizedRoutingStrategy
import com.nokia.dempsy.serialization.kryo.KryoSerializer
import com.nokia.dempsy.monitoring.coda.StatsCollectorFactoryCoda
import com.nokia.dempsy.messagetransport.blockingqueue.BlockingQueueTransport

import groovy.transform.*

@TupleConstructor
class Money {
	String moneyText

	@MessageKey
	String getMoneyText() {
		moneyText
	}
}

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

def mp = new ClusterDefinition('mp', new MoneyCount())
// 終了時に結果を出力
mp.outputExecuter = [
	setOutputInvoker: { invoker ->
		println 'OutputInvoker.setOutputInvoker'
		this.invoker = invoker
	},
	start: { ->
		println 'OutputInvoker.start'
	},
	stop: { ->
		println 'OutputInvoker.stop'
		// 処理結果を出力
		invoker.invokeOutput()
	}
] as OutputExecuter


def dempsy = new Dempsy()

def app = new ApplicationDefinition('money-count').add(
	new ClusterDefinition('adaptor', new MoneyAdaptor(new File(args[0]))),
	mp
)

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
