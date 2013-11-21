@Grab('org.projectreactor:reactor-core:1.0.0.RELEASE')
@Grab('org.projectreactor:reactor-groovy:1.0.0.RELEASE')
@Grab('org.slf4j:slf4j-nop:1.7.5')
import reactor.core.Environment
import reactor.core.spec.Reactors

def env = new Environment()

def r = Reactors.reactor(env)

r.on('msg') { String msg ->
	println "received: ${msg}"
}

r.notify 'msg', 'test data'

env.shutdown()
