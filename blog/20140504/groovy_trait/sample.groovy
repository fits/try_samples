
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


println '----- 1 -----'

def s1 = new Sample1(name: 'S1', price: 100, qty: 5)
println "${s1}, total: ${s1.total()}, price: ${s1.price}"
println s1.dump()

println ''
println '----- 2a -----'

def s2a = new Sample2(name: 'S2a', price: 200, qty: 6)
// price や qty へ値が設定されず total = 0 になる
println "${s2a}, total: ${s2a.total()}, price: ${s2a.price}"
println s2a.dump()
// トレイトで定義したプロパティは値を書き込める
s2a.price = 300
s2a.qty = 3
println "${s2a}, total: ${s2a.total()}, price: ${s2a.price}"
println s2a.dump()

println ''
println '----- 2b -----'

def s2b = new Sample2(name: 'S2b', BasicPricing__price: 200, QuantityPricing__qty: 6)
println "${s2b}, total: ${s2b.total()}, price: ${s2b.price}"
println s2b.dump()
