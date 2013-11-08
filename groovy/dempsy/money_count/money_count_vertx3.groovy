@Grab('net.dempsy:lib-dempsyimpl:0.7.9')
@Grab('org.slf4j:slf4j-log4j12:1.7.5')
@Grab('io.vertx:vertx-core:2.1M1')
@Grab('io.vertx:lang-groovy:2.0.0-final')
import com.nokia.dempsy.annotations.*
import com.nokia.dempsy.*
import com.nokia.dempsy.config.*
import com.nokia.dempsy.output.*

import com.nokia.dempsy.router.SpecificClusterCheck
import com.nokia.dempsy.router.DecentralizedRoutingStrategy
import com.nokia.dempsy.serialization.kryo.KryoSerializer
import com.nokia.dempsy.monitoring.coda.StatsCollectorFactoryCoda
import com.nokia.dempsy.messagetransport.tcp.TcpTransport

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
		println '*** countup'
		count++
	}

	@Output
	void printResults() {
		println "key: ${key}, count: ${count}"
	}
}

class MoneyAdaptor implements Adaptor {
	final static def MONEYS = [
		'1', '5', '10', '50', '100', '500', '1000', '2000', '5000', '10000'
	]

	Dispatcher dispatcher

	void start() {
		println 'MoneyAdaptor.start ...'

		def vertx = Vertx.newVertx()

		def rm = new RouteMatcher()
		rm.get '/:money', { req ->
			def money = req.params['money']

			if (MONEYS.contains(money)) {
				println "*** dispatch $money : ${dispatcher}"
				dispatcher.dispatch(new Money(money))
			}
			req.response.end()
		}

		vertx.createHttpServer().requestHandler(rm.asClosure()).listen 8080
	}

	void stop() {
		println 'MoneyAdaptor.stop'
	}
}


def cluster = args[0]

def dempsy = new Dempsy()

def mp = new ClusterDefinition('mp', new MoneyCount())
mp.outputExecuter = new com.nokia.dempsy.output.RelativeOutputSchedule(10, java.util.concurrent.TimeUnit.SECONDS)

def app = new ApplicationDefinition('money-count').add(
	new ClusterDefinition('adaptor', new MoneyAdaptor()),
	mp
)

dempsy.applicationDefinitions = [app]
dempsy.clusterSessionFactory = new com.nokia.dempsy.cluster.zookeeper.ZookeeperSessionFactory('localhost:2181', 5000)
dempsy.clusterCheck = new SpecificClusterCheck(new com.nokia.dempsy.config.ClusterId('money-count', cluster))
// mp を 3ノード構成で処理する設定
dempsy.defaultRoutingStrategy = new DecentralizedRoutingStrategy(MoneyAdaptor.MONEYS.size(), 3)
dempsy.defaultSerializer = new KryoSerializer()
dempsy.defaultStatsCollectorFactory = new StatsCollectorFactoryCoda()
dempsy.defaultTransport = new TcpTransport()

dempsy.start()

Runtime.runtime.addShutdownHook { ->
	println 'shutdown ...'
	dempsy.stop()
}

dempsy.waitToBeStopped()
