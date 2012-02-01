@Grab("org.drools:drools-core:5.4.0.Beta1")
@Grab("org.drools:drools-compiler:5.4.0.Beta1")
@Grab("com.sun.xml.bind:jaxb-xjc:2.2.5-b09")
import org.drools.KnowledgeBaseFactory
import org.drools.builder.KnowledgeBuilderFactory
import org.drools.builder.ResourceType
import org.drools.io.ResourceFactory

def drl = '''
dialect "mvel"

rule "ルール1"
    no-loop
    salience 20
    date-effective "2012-02-02 00:30:00"
    date-expires "2012-02-03 00:30:00"
    when
        $d : Data(done == false)
    then
        System.out.println("*** ルール1")
        $d.setDone(true)
        update($d)
end

rule "ルール2"
    no-loop
    salience 10
    when
        $d : Data(done == false)
    then
        System.out.println("*** ルール2")
        $d.setDone(true)
        update($d)
end
'''

class Data {
    def done = false
}

def createSession = {drlString ->
    def builder = KnowledgeBuilderFactory.newKnowledgeBuilder()

    builder.add(ResourceFactory.newReaderResource(new StringReader(drlString)), ResourceType.DRL)

    if (builder.hasErrors()) {
        println builder.errors
        throw new RuntimeException("invalid DRL")
    }

    def base = KnowledgeBaseFactory.newKnowledgeBase()
    base.addKnowledgePackages(builder.getKnowledgePackages())

    base.newStatefulKnowledgeSession()
}

//Drools デフォルトの日付フォーマットを変更
System.setProperty("drools.dateformat", "yyyy-MM-dd HH:mm:ss")

def session = createSession(drl)

session.insert(new Data())

session.fireAllRules()
session.dispose()
