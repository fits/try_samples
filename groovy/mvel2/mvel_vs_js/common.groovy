// íçï∂
class Order {
	String no
	List<OrderItem> items = []
	PaymentMethod payment = new PaymentMethod()
	// ëóóø
	BigDecimal postage = 0

	BigDecimal getTotalPrice() {
		postage + payment.commission + items.inject(0) { acc, item ->
			acc + item.totalPrice
		}
	}

	@Override
	String toString() {
		"Order( no: ${no}, postage: ${postage}, payment: ${payment}, total: ${totalPrice} )" + items.inject("") { acc, item -> "${acc}\n${item.toString()}" }
	}
}

// íçï∂ñæç◊
class OrderItem {
	Product product = new Product()
	BigDecimal qty = 1

	BigDecimal getPrice() {
		product.price
	}

	BigDecimal getTotalPrice() {
		getPrice() * qty
	}

	@Override
	String toString() {
		"- OrderItem( product: ${product}, qty: ${qty}, total: ${totalPrice} )"
	}
}

// è§ïi
class Product {
	String name
	BigDecimal price = 0

	@Override
	String toString() {
		"Product( name: ${name}, price: ${price} )"
	}
}

// éxï•ï˚ñ@
class PaymentMethod {
	String name
	// éËêîóø
	BigDecimal commission = 0

	@Override
	String toString() {
		"PaymentMethod( name: ${name}, commission: ${commission} )"
	}
}
