@Grab('com.github.shyiko:mysql-binlog-connector-java:0.13.0')
@Grab('io.reactivex.rxjava2:rxjava:2.1.5')
import static com.github.shyiko.mysql.binlog.event.EventType.*
import com.github.shyiko.mysql.binlog.BinaryLogClient

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

def targetEvents = [
	WRITE_ROWS,
	UPDATE_ROWS,
	DELETE_ROWS,
	EXT_WRITE_ROWS,
	EXT_UPDATE_ROWS,
	EXT_DELETE_ROWS
]

def host = args[0]
def port = args[1] as int
def user = args[2]
def pass = args[3]

def client = new BinaryLogClient(host, port, user, pass)

addShutdownHook {
	client.disconnect()
}

def binlogSubject = PublishSubject.create()

client.registerLifecycleListener([
	onConnect: { cl ->
		println '*** onConnect'
		cl.registerEventListener { binlogSubject.onNext(it) }
	},
	onDisconnect: { cl -> 
		println '*** onDisconnect'
		binlogSubject.onComplete()
	},
	onCommunicationFailure: { cl, err ->
		println '*** onCommunicationFailure'
		binlogSubject.onError(err)
	},
	onEventDeserializationFailure: { cl, err ->
		println '*** onEventDeserializationFailure'
		binlogSubject.onError(err)
	}
] as BinaryLogClient.LifecycleListener)

def tableMapSubject = PublishSubject.create()
def rowSubject = PublishSubject.create()

binlogSubject.filter { it.header.eventType == TABLE_MAP }
	.scan([:]) { acc, ev ->
		acc[ev.data.tableId] = ev.data
		acc
	}
	.subscribe(tableMapSubject)

binlogSubject.filter { targetEvents.contains(it.header.eventType) }
	.subscribe(rowSubject)

tableMapSubject.zipWith(rowSubject) { t, r ->
	[
		table: t[r.data.tableId],
		data: r.data
	]
}.subscribe { println it }

//binlogSubject.subscribe { println it }

client.connect()
