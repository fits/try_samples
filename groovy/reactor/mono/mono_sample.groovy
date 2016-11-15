@Grab('io.projectreactor:reactor-core:3.0.3.RELEASE')
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

def dump = { println "${it}, thread: ${Thread.currentThread()}" }

println '--- (a) ---'

Mono.just('(a)')
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
	.subscribe { dump "${it}_subscribe" }

println '--- (b) ---'

Mono.just('(b)')
	.map {
		def n = "${it}-${System.currentTimeMillis()}"
		dump("${n}_map1")
		n
	}
	.publishOn(Schedulers.single())
	.repeat(3)
	.map {
		dump("${it}_map2")
		sleep(2000)
		it
	}
	.subscribe { dump "${it}_subscribe" }

println '--- (c) ---'

Mono.just('(c)')
	.map {
		def n = "${it}-${System.currentTimeMillis()}"
		dump("${n}_map1")
		n
	}
	.repeat(3)
	.publishOn(Schedulers.single())
	.map {
		dump("${it}_map2")
		sleep(2000)
		it
	}
	.subscribe { dump "${it}_subscribe" }

println '--- (d) ---'

Mono.just('(d)')
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
	.subscribe { dump "${it}_subscribe" }

println '--- (e) ---'

Mono.just('(e)')
	.map {
		def n = "${it}-${System.currentTimeMillis()}"
		dump("${n}_map1")
		n
	}
	.repeat(3)
	.parallel(3)
	.runOn(Schedulers.parallel())
	.map {
		dump("${it}_map2")
		sleep(2000)
		it
	}
	.subscribe { dump "${it}_subscribe" }



println '-----------'

System.in.read()
