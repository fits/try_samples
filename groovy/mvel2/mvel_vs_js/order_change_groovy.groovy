import java.math.*

def order = new Order(no: '1', postage: 525)
order.payment.commission = 315

order.items << new OrderItem(product: new Product(name: 'A1', price: 1000), qty: 2)
order.items << new OrderItem(product: new Product(name: 'B1', price: 500))
order.items << new OrderItem(product: new Product(name: 'C2', price: 700))

println order
println '--------------------------'

def start = System.currentTimeMillis()

order.postage = 0
order.payment.commission = 0

order.items.each {
	if (it.qty > 1) {
		it.product.price = it.product.price.multiply(0.75, new MathContext(0, RoundingMode.DOWN))
	}

	if (it.product.name.contains("1")) {
		it.product.price = it.product.price.multiply(0.5, new MathContext(0, RoundingMode.DOWN))
	}
}

def end = System.currentTimeMillis()

println order

println '--------------------------'

println "***** eval time : ${end - start}ms"
