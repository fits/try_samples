@Grab('org.projectreactor:reactor-groovy:1.0.0.RELEASE')
import reactor.core.composable.Deferred
import reactor.core.composable.Stream
import reactor.core.composable.spec.Streams

Deferred  d = Streams.defer(1..10).get()

Stream s = d.compose()
s.consume { 
	println it
}

s.flush()
