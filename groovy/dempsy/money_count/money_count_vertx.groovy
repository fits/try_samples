@Grab('net.dempsy:lib-dempsyimpl:0.7.9')
@Grab('org.slf4j:slf4j-log4j12:1.7.5')
@Grab('io.vertx:vertx-core:2.1M1')
@Grab('io.vertx:lang-groovy:2.0.0-final')
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

import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.http.RouteMatcher

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
	private final static def MONEYS = [
		'1', '5', '10', '100', '500', '1000', '2000', '5000', '10000'
	]
	Dispatcher dispatcher

	void start() {
		println 'MoneyAdaptor.start ...'

		def vertx = Vertx.newVertx()

		def rm = new RouteMatcher()
		rm.get '/:money', { req ->
			def money = req.params['money']

			if (MONEYS.contains(money)) {
				dispatcher.dispatch(new Money(req.params['money']))
			}
			req.response.end()
		}

		vertx.createHttpServer().requestHandler(rm.asClosure()).listen 8080
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
	new ClusterDefinition('adaptor', new MoneyAdaptor()),
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

System.in.read()

dempsy.stop()
