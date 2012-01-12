@Grab("org.drools:drools-core:5.4.0.Beta1")
@Grab("org.drools:drools-compiler:5.4.0.Beta1")
@Grab("com.sun.xml.bind:jaxb-xjc:2.2.5-b09")
import org.drools.KnowledgeBaseFactory
import org.drools.builder.KnowledgeBuilderFactory
import org.drools.builder.ResourceType
import org.drools.io.ResourceFactory

import groovy.transform.AutoClone
import groovy.text.GStringTemplateEngine

/*
 * Drools のルール定義 DRL をテンプレートエンジンを使って動的に作成して
 * ルールを適用するサンプルスクリプト
 *
 */

//ルール定義のテンプレート
def drl = '''
dialect "mvel"

<% patterns.each { %>
	rule "${it.title}"
		no-loop
		salience ${it.priority}
		when
			\\$order : Order()
			<% it.condition.eachWithIndex {c, i -> %>
				\\$item${i} : Product(${c}
					<% if (i > 0) { %>
					, this not in (<%= (0..<i).collect{"\\$item${it}"}.join(',') %>)
					<% } %>
					, discount == false)
			<% } %>
		then
			\\$cp = new DiscountCampaign("${it.title}")
			\\$cp.${it.action}

			<% it.condition.eachWithIndex {c, i -> %>
				\\$cp.getItemList().add(\\$item${i})
				\\$item${i}.setDiscount(true)
			<% } %>

			\\$order.getPriceList().add(\\$cp)

			update(\\$order)

			<% it.condition.eachWithIndex {c, i -> %>
				update(\\$item${i})
			<% } %>
	end
<% } %>
'''


interface Pricer {
	String getName()
	int totalPrice()
}

//商品
@AutoClone
class Product implements Pricer {
	def category
	def id
	String name
	int price = 0
	boolean discount = false

	int totalPrice() {
		price
	}
}

//注文
class Order implements Pricer {
	String name = ""
	List<OrderItem> itemList = []
	List<Pricer> priceList = []

	int totalPrice() {
		calculate(priceList.isEmpty()? itemList: priceList)
	}

	private int calculate(list) {
		list.inject(0) {acc, val -> acc + val.totalPrice()}
	}
}

//注文明細
class OrderItem implements Pricer {
	String name = ""
	Product product
	int qty

	int totalPrice() {
		qty * product.totalPrice()
	}
}

//割引キャンペーン
class DiscountCampaign implements Pricer {
	def baseName = ""
	def price = 0
	def rate = 0.0
	List<Product> itemList = []

	DiscountCampaign(String baseName) {
		this.baseName = baseName
	}

	String getName() {
		"${baseName} - [" + itemList.collect{"${it.name}"}.join(', ') + "]"
	}

	int totalPrice() {
		(rate == 0.0)? price: itemList.inject(0) {acc, val -> acc + val.totalPrice()} * rate
	}
}

//割引キャンペーン適用
class DiscountCampaignBuilder {
	def drlString
	def bindParams = [:]

	def apply(Order order) {
		def session = createSession()

		if (session) {
			session.insert(order)

			def items = divideItems(order.itemList)

			items.each {
				session.insert(it)
			}

			//ルールの適用
			session.fireAllRules()
			session.dispose()

			//割引キャンペーンが適用されなかった商品を追加
			order.priceList += items.findAll {it.discount == false}
		}
	}

	//注文明細を商品 1点単位に分割（価格の高い順）
	def divideItems(itemList) {
		itemList.collect {item ->
			(0..<item.qty).collect {
				item.product.clone()
			}
		}.flatten().sort({a, b -> b.totalPrice() <=> a.totalPrice()})
	}

	def createSession() {
		def builder = KnowledgeBuilderFactory.newKnowledgeBuilder()

		//テンプレート処理を行ってルール定義を生成
		def drl = new GStringTemplateEngine().createTemplate(drlString).make(bindParams).toString()

		println drl

		builder.add(ResourceFactory.newReaderResource(new StringReader(drl)), ResourceType.DRL)

		if (builder.hasErrors()) {
			println builder.errors
			return
		}

		def base = KnowledgeBaseFactory.newKnowledgeBase()
		base.addKnowledgePackages(builder.getKnowledgePackages())

		base.newStatefulKnowledgeSession()
	}
}


def order = new Order()

def item1 = new OrderItem(qty: 4, product: new Product(category: "A", id: "001", name: "テスト商品1", price: 4000))
order.itemList.add(item1)

def item2 = new OrderItem(qty: 3, product: new Product(category: "B", id: "002", name: "テスト商品2", price: 3000))
order.itemList.add(item2)

def item3 = new OrderItem(qty: 6, product: new Product(category: "C", id: "003", name: "テスト商品3", price: 3500))
order.itemList.add(item3)

def item4 = new OrderItem(qty: 2, product: new Product(category: "D", id: "004", name: "テスト商品4", price: 4500))
order.itemList.add(item4)


def builder = new DiscountCampaignBuilder(drlString: drl)

//割引キャンペーンのパラメータ定義
builder.bindParams["patterns"] = [
	[
		title: "セットキャンペーン1（A1点 + B1点）",
		priority: 30,
		condition: [
			'category == "A"',
			'category == "B"'
		], 
		action: 'setPrice(5500)'
	],
	[
		title: "セットキャンペーン2（C1点 + A1点）",
		priority: 20,
		condition: [
			'category == "C"',
			'category == "A"'
		], 
		action: 'setPrice(6000)'
	],
	[
		title: "セットキャンペーン3（C1点 + A1点 + 何でも 1点）",
		priority: 20,
		condition: [
			'category == "C"',
			'category == "A"',
			'true'
		], 
		action: 'setRate(0.8)'
	],
	[
		title: "セットキャンペーン4（A以外で 4000円以上 1点と A以外 1点）",
		priority: 10,
		condition: [
			'category != "A" && price >= 4000',
			'category != "A"'
		], 
		action: 'setRate(0.9)'
	]
]

def beforePrice = order.totalPrice()

//割引キャンペーン適用
builder.apply(order)

println "割引前 合計金額 = ${beforePrice}"
println "割引後 合計金額 = ${order.totalPrice()}"

order.priceList.each {
	println "内訳 : ${it.name} = ${it.totalPrice()}"
}

