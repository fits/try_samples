
def items = [
	new ProductItem("id1", "商品1", new BigDecimal("1000"), 1),
	new ProductItem("id2", "商品2", new BigDecimal("3000"), 2),
	new ProductItem("id3", "商品3", new BigDecimal("1500"), 3)
]
// (1) マッピング
def names = items.collect { it.name }

names.each { println it }

println "-----"

// (2) フィルタリング
def highItems = items.findAll { it.getPrice().compareTo(new BigDecimal("1500")) >= 0 }

highItems.each { println it.name }

println "-----"

// (3) 畳み込み
def total = items.inject(0) {a, b ->
	a + b.price * b.qty
}

println total
