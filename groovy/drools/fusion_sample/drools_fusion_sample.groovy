package sample

@Grapes([
	@Grab("org.drools:drools-core:5.4.0.Beta1"),
	@Grab("org.drools:drools-compiler:5.4.0.Beta1"),
	@Grab("com.sun.xml.bind:jaxb-xjc:2.2.5-b09")
])
import org.drools.KnowledgeBaseFactory
import org.drools.conf.EventProcessingOption
import org.drools.builder.KnowledgeBuilderFactory
import org.drools.builder.ResourceType
import org.drools.io.ResourceFactory

class Data {
	String name
	int point
}

def builder = KnowledgeBuilderFactory.newKnowledgeBuilder()

builder.add(ResourceFactory.newClassPathResource(args[0], getClass()), ResourceType.DRL)

if (builder.hasErrors()) {
	println builder.errors
}


def base = KnowledgeBaseFactory.newKnowledgeBase()
base.addKnowledgePackages(builder.getKnowledgePackages())

def session = base.newStatefulKnowledgeSession()
def stream = session.getWorkingMemoryEntryPoint("sample stream")

stream.insert(new Data(name: "data1", point: 5))
stream.insert(new Data(name: "data2", point: 10))
stream.insert(new Data(name: "data3", point: 1))
stream.insert(new Data(name: "data4", point: 25))

session.fireAllRules()
session.dispose()
