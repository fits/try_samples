@Grab('org.mvel:mvel2:2.1.6.Final')
import org.mvel2.MVEL

def order = new Order(no: '1', postage: 525)
order.payment.commission = 315

order.items << new OrderItem(product: new Product(name: 'A1', price: 1000), qty: 2)
order.items << new OrderItem(product: new Product(name: 'B1', price: 500))
order.items << new OrderItem(product: new Product(name: 'C2', price: 700))

println order
println '--------------------------'

def exp = '''
import java.math.*;

order.postage = 0;
order.payment.commission = 0;

for (item : order.items) {
	if (item.qty > 1) {
		item.product.price = item.product.price.multiply(0.75, new MathContext(0, RoundingMode.DOWN))
//		item.product.price = item.product.price * 0.75
	}

	if (item.product.name contains "1") {
		item.product.price = item.product.price.multiply(0.5, new MathContext(0, RoundingMode.DOWN))
//		item.product.price = item.product.price * 0.5
	}
}
'''

def ser = MVEL.compileExpression(exp)

def start = System.currentTimeMillis()

MVEL.executeExpression(ser, [order: order])

def end = System.currentTimeMillis()

println order

println '--------------------------'

println "***** eval time : ${end - start}ms"
