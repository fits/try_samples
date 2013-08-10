import javax.script.*

def order = new Order(no: '1', postage: 525)
order.payment.commission = 315

order.items << new OrderItem(product: new Product(name: 'A1', price: 1000), qty: 2)
order.items << new OrderItem(product: new Product(name: 'B1', price: 500))
order.items << new OrderItem(product: new Product(name: 'C2', price: 700))

println order
println '--------------------------'

def exp = '''
importClass(java.math.BigDecimal);
importClass(java.math.MathContext);
importClass(java.math.RoundingMode);

order.postage = BigDecimal.ZERO;
order.payment.commission = BigDecimal.ZERO;

for (var i = 0; i < order.items.size(); i++) {
	var item = order.items.get(i);

	if (item.qty > 1) {
		item.product.price = item.product.price.multiply(new BigDecimal("0.75"), new MathContext(0, RoundingMode.DOWN));
	}

	if (item.product.name.indexOf("1") > -1) {
		item.product.price = item.product.price.multiply(new BigDecimal("0.5"), new MathContext(0, RoundingMode.DOWN));
	}
}
'''

def start = System.currentTimeMillis()

def engine = new ScriptEngineManager().getEngineByExtension('js')
def bind = engine.getBindings(ScriptContext.ENGINE_SCOPE)

bind.put('order', order)

engine.eval(exp)

def end = System.currentTimeMillis()

println order

println '--------------------------'

println "***** eval time : ${end - start}ms"
