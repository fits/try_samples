@Grab('com.github.msemys:esjc:1.6.0')
@Grab('org.slf4j:slf4j-simple:1.7.22')
import com.github.msemys.esjc.EventStoreBuilder
import com.github.msemys.esjc.EventData
import com.github.msemys.esjc.ExpectedVersion

import java.util.concurrent.Executors

def es = Executors.newCachedThreadPool()

def estore = EventStoreBuilder.newBuilder()
			.singleNodeAddress('127.0.0.1', 1113)
			.userCredentials('admin', 'changeit')
			.executor(es) // needed to end
			.build()

def any = ExpectedVersion.any()

def data = EventData.newBuilder()
			.type('sample-event')
			.data('sample data1')
			.build()

estore.appendToStream('sample1', any, data).thenAccept { 
	println it.dump()

	estore.disconnect()

	es.shutdown()

	// needed to end
	estore.group.shutdownGracefully()
}
