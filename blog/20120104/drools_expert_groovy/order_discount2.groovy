package fits.sample

@Grab("org.drools:drools-core:5.4.0.Beta1")
@Grab("org.drools:drools-compiler:5.4.0.Beta1")
@Grab("com.sun.xml.bind:jaxb-xjc:2.2.5-b09")
import org.drools.KnowledgeBaseFactory
import org.drools.builder.KnowledgeBuilderFactory
import org.drools.builder.ResourceType
import org.drools.io.ResourceFactory

class Order {
	String name
	BigDecimal subTotalPrice = BigDecimal.ZERO
	BigDecimal discountPrice = BigDecimal.ZERO

	BigDecimal totalPrice() {
		subTotalPrice.subtract(discountPrice)
	}
}

def builder = KnowledgeBuilderFactory.newKnowledgeBuilder()

builder.add(ResourceFactory.newClassPathResource("order_discount2.drl", getClass()), ResourceType.DRL)

if (builder.hasErrors()) {
	println builder.errors
	return
}

def base = KnowledgeBaseFactory.newKnowledgeBase()
base.addKnowledgePackages(builder.getKnowledgePackages())

def session = base.newStatelessKnowledgeSession()

def orders = [
	new Order(name: "order1", subTotalPrice: new BigDecimal(2000)),
	new Order(name: "order2", subTotalPrice: new BigDecimal(5000)),
	new Order(name: "order3", subTotalPrice: new BigDecimal(10000)),
	new Order(name: "order4", subTotalPrice: new BigDecimal(13000)),
	new Order(name: "order5", subTotalPrice: new BigDecimal(18000))
]

session.execute(orders)

println "--------------------"

orders.each {
	println "${it.name} : ${it.totalPrice()}"
}
