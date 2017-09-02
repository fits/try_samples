@GrabResolver(name = 'bintray', root = 'https://jcenter.bintray.com')
@Grab('io.moquette:moquette-broker:0.10')
@Grab('org.slf4j:slf4j-simple:1.7.25')
import io.moquette.server.Server

Server.main(args)
