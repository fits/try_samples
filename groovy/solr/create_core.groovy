@Grab('org.apache.solr:solr-core:5.4.1')
@Grab('org.slf4j:slf4j-nop:1.7.16')
import org.apache.solr.core.CoreContainer
import org.apache.solr.core.CoreDescriptor

import java.nio.file.Paths

def solrHome = args[0]
def name = args[1]
def dir = args[2]

def cores = CoreContainer.createAndLoad(Paths.get(solrHome))

def desc = new CoreDescriptor(cores, name, dir, 'configSet', 'basic_configs')

def core = cores.create(desc)

// create core.properties
cores.getCoresLocator().create(cores, desc)

println "created: ${core.name}"

println '----- cores ----'

cores.getCores().each {
	println it.name
}

cores.shutdown()
