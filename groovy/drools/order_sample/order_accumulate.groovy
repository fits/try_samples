@Grab("org.drools:drools-core:5.4.0.Beta1")
@Grab("org.drools:drools-compiler:5.4.0.Beta1")
@Grab("com.sun.xml.bind:jaxb-xjc:2.2.5-b09")
import org.drools.KnowledgeBaseFactory
import org.drools.builder.KnowledgeBuilderFactory
import org.drools.builder.ResourceType
import org.drools.io.ResourceFactory

/*
 * DRL 内で明細を集計処理するサンプル
 *
 *
 */

//ルール定義1
def drl1 = '''
dialect "mvel"

rule "A カテゴリを全体で 5千円以上購入すると送料無料"
    no-loop
    when
        $order : Order()
        //A カテゴリで注文明細を集計して 5000 以上のパターン定義
        $total : Number(doubleValue >= 5000) from accumulate(
            OrderItem(
                $price : totalPrice(), product.category == "A"
            ) from $order.getItemList(),
            sum($price)
        )
    then
        System.out.println("** 送料無料 : Aカテゴリ購入金額=" + $total)
        $order.setSippingCost(0)
end
'''

//注文
class Order {
    def sippingCost = 500
    List<OrderItem> itemList = []

    int totalPrice() {
        itemList.inject(0) {acc, val -> acc + val.totalPrice()} + sippingCost
    }
}
//注文明細
class OrderItem {
    Product product
    int qty

    int totalPrice() {
        qty * product.price
    }
}
//商品
class Product {
    def category
    def name
    def price = 0
}

def order = new Order()

order.itemList.add(new OrderItem(qty: 1, product: new Product(category: "A", name: "テスト商品1", price: 4000)))

order.itemList.add(new OrderItem(qty: 3, product: new Product(category: "B", name: "テスト商品2", price: 3000)))

order.itemList.add(new OrderItem(qty: 2, product: new Product(category: "A", name: "テスト商品3", price: 1500)))


def builder = KnowledgeBuilderFactory.newKnowledgeBuilder()

builder.add(ResourceFactory.newReaderResource(new StringReader(drl1)), ResourceType.DRL)

if (builder.hasErrors()) {
    println builder.errors
    return
}

def base = KnowledgeBaseFactory.newKnowledgeBase()
base.addKnowledgePackages(builder.getKnowledgePackages())

def session = base.newStatefulKnowledgeSession()

println "ルール適用前： ${order.dump()}"

session.insert(order)

session.fireAllRules()
session.dispose()

println "ルール適用後： ${order.dump()}"

