@GrabResolver('http://repo.spring.io/libs-snapshot')
@Grab('org.projectreactor:reactor-groovy:2.0.0.BUILD-SNAPSHOT')
@Grab('org.slf4j:slf4j-api:1.7.7')
@Grab('org.slf4j:slf4j-simple:1.7.7')
import reactor.core.Environment
import reactor.rx.spec.Streams

def env = new Environment()

def stream = Streams.defer(env)

//stream.map { "#${it}" }.consume { println it }
//stream.first(5).map { "#${it}" }.consume { println it }
stream.filter { it.trim().length() > 0 }.map { "#${it}" }.consume { println it }

new File(args[0]).eachLine {
	stream.broadcastNext it
}

env.shutdown()
