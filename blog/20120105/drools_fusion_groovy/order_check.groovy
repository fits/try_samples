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

builder.add(ResourceFactory.newClassPathResource("order_check.drl", getClass()), ResourceType.DRL)

if (builder.hasErrors()) {
	println builder.errors
	return
}

def base = KnowledgeBaseFactory.newKnowledgeBase()
base.addKnowledgePackages(builder.getKnowledgePackages())

def session = base.newStatefulKnowledgeSession()

(0..<10).each {
	def order = new Order(name: "order${it}", subTotalPrice: new BigDecimal((int)Math.random() * 10000))
	println("order : ${order.name}, ${order.totalPrice()}")

	session.insert(order)

	session.fireAllRules()
	Thread.sleep(1000)
}

session.dispose()
