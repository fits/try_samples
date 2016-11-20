@Grab('io.reactivex.rxjava2:rxjava:2.0.1')
import io.reactivex.Single
import io.reactivex.Flowable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

def dump = { println "${it}, thread: ${Thread.currentThread()}" }

println '--- (a) ---'

Single.just('(a)')
	.map {
		def n = "${it}-${System.currentTimeMillis()}"
		dump "${n}_map1"
		n
	}
	.repeat(3)
	.map {
		dump("${it}_map2")
		sleep(2000)
		it
	}
	.subscribe( { dump "${it}_subscribe" } as Consumer )

println '--- (b) ---'

Single.just('(b)')
	.map {
		def n = "${it}-${System.currentTimeMillis()}"
		dump("${n}_map1")
		n
	}
	.observeOn(Schedulers.single())
	.repeat(3)
	.map {
		dump("${it}_map2")
		sleep(2000)
		it
	}
	.subscribe( { dump "${it}_subscribe" } as Consumer )

println '--- (c) ---'

Single.just('(c)')
	.map {
		def n = "${it}-${System.currentTimeMillis()}"
		dump("${n}_map1")
		n
	}
	.repeat(3)
	.observeOn(Schedulers.single())
	.map {
		dump("${it}_map2")
		sleep(2000)
		it
	}
	.subscribe( { dump "${it}_subscribe" } as Consumer )

println '--- (d) ---'

Single.just('(d)')
	.map {
		def n = "${it}-${System.currentTimeMillis()}"
		dump("${n}_map1")
		n
	}
	.repeat(3)
	.map {
		dump("${it}_map2")
		sleep(2000)
		it
	}
	.subscribeOn(Schedulers.single())
	.subscribe( { dump "${it}_subscribe" } as Consumer )

println '--- (e) ---'

Single.just('(e)')
	.repeat(3)
	.flatMap {
		Flowable.just(it)
			.map {
				def n = "${it}-${System.currentTimeMillis()}"
				dump("${n}_map1")
				n
			}
			.map {
				dump("${it}_map2")
				sleep(2000)
				it
			}
			.subscribeOn(Schedulers.newThread())
	}
	.subscribe( { dump "${it}_subscribe" } as Consumer )

System.in.read()
