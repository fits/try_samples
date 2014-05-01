
import groovy.transform.*

interface Pricing {
	BigDecimal total()
}

trait BasicPricing implements Pricing {
	BigDecimal price = 0

	BigDecimal total() {
		price
	}
}

trait QuantityPricing extends BasicPricing {
	int qty

	BigDecimal total() {
		price * qty
	}
}

@ToString
class Sample1 implements QuantityPricing {
	String name
}

@Immutable
class Sample2 implements QuantityPricing {
	String name
}

def s1 = new Sample1(name: 'S1', price: 100, qty: 5)
// Sample1(S1), 500
println "${s1}, ${s1.total()}"

def s2 = new Sample2(name: 'S2', price: 200, qty: 6)
// @Immutable の場合は trait のフィードルへ値を設定できない
// Sample2(S2), 0
println "${s2}, ${s2.total()}"
