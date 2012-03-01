@Grab("org.drools:drools-core:5.4.0.Beta2")
@Grab("org.drools:drools-compiler:5.4.0.Beta2")
@Grab("com.sun.xml.bind:jaxb-xjc:2.2.5-b09")
import org.drools.KnowledgeBaseFactory
import org.drools.builder.KnowledgeBuilderFactory
import org.drools.builder.ResourceType
import org.drools.io.ResourceFactory

def drl = '''
    import java.math.BigDecimal

    dialect "mvel"

    rule "カテゴリ AA から始まる場合に 5百円引き"
        no-loop
        salience 10
        when
            $item : ItemParameter(done == false, category matches "AA.*")
            //以下でも可
            //$item : ItemParameter(done == false, category.startsWith("AA"))
        then
            System.out.println("値引き : " + $item.getName());
            $item.setPrice($item.getPrice().subtract(new BigDecimal("500")));
            $item.setDone(true);

            update($item);
    end

    rule "対象外"
        no-loop
        salience 1
        when
            $item : ItemParameter(done == false)
        then
            System.out.println("対象外 : " + $item.getName)

            $item.setDone(true)
            update($item)
    end
'''

class ItemParameter {
    String category
    String name
    BigDecimal price
    boolean done = false
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

def session = createSession(drl)

//注文内容
def inputData = [
    new ItemParameter(category: "AABB", name: "アイテム1", price: 4000),
    new ItemParameter(category: "BAAC", name: "アイテム2", price: 1000),
    new ItemParameter(category: "AABB", name: "アイテム3", price: 4000),
    new ItemParameter(category: "CCCE", name: "アイテム4", price: 2000),
    new ItemParameter(category: "ABDA", name: "アイテム5", price: 1300),
    new ItemParameter(category: "AAWQ", name: "アイテム6", price: 2000),
    new ItemParameter(category: "CAAQ", name: "アイテム7", price: 2000)
]

inputData.each {
    session.insert(it)
}

session.fireAllRules()
session.dispose()

println "--- 結果 ---"
inputData.each {
    println "${it.category}, ${it.name}, ${it.price}, ${it.done}"
}
