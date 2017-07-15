@Grab('io.projectreactor:reactor-core:3.0.7.RELEASE')
import reactor.core.publisher.*

def d = Mono.just(['a', 'b', 'c'])

d.subscribe {
	println it
}

println '----------'

def s = d.flatMap { Flux.fromIterable(it) }

s.subscribe {
	println it
}

println '----------'

def s2 = d.flux().flatMap { Flux.fromIterable(it) }

s2.subscribe {
	println it
}
