@Grab("org.drools:drools-core:5.4.0.Beta2")
@Grab("org.drools:drools-compiler:5.4.0.Beta2")
@Grab("com.sun.xml.bind:jaxb-xjc:2.2.5-b09")
import org.drools.KnowledgeBaseFactory
import org.drools.builder.KnowledgeBuilderFactory
import org.drools.builder.ResourceType
import org.drools.io.ResourceFactory

def drl = '''
    import java.math.BigDecimal
    import java.util.concurrent.CopyOnWriteArrayList

    dialect "mvel"

    rule "カテゴリ A 2点以上で 1点毎に 5百円引き"
        no-loop
        salience 10
        when
            //ArrayList を使った場合 ConcurrentModificationException が発生
            //$items : java.util.ArrayList( size >= 2 )
            $items : CopyOnWriteArrayList( size >= 2 )
            from collect (ItemParameter(done == false, category == "A"))
        then
            System.out.println("*** 値引き処理 ***")

            for (ItemParameter $item : $items) {
                System.out.println("値引き : " + $item.getName());
                $item.setPrice($item.getPrice().subtract(new BigDecimal("500")));
                $item.setDone(true);

                update($item);
            }
    end

    rule "対象外"
        no-loop
        salience 1
        when
            $item : ItemParameter(done == false)
        then
            System.out.println("*** 値引き対象外 ***")
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
    new ItemParameter(category: "A", name: "アイテム1", price: 4000),
    new ItemParameter(category: "B", name: "アイテム2", price: 1000),
    new ItemParameter(category: "A", name: "アイテム3", price: 4000),
    new ItemParameter(category: "C", name: "アイテム4", price: 2000),
    new ItemParameter(category: "A", name: "アイテム5", price: 1300),
    new ItemParameter(category: "A", name: "アイテム6", price: 2000),
    new ItemParameter(category: "C", name: "アイテム7", price: 2000)
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
