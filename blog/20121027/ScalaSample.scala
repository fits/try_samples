
import scala.math.BigDecimal._

val items = List(
	new ProductItem("id1", "商品1", BigDecimal("1000").bigDecimal, 1),
	//以下でも可
	//new ProductItem("id1", "商品1", 1000.bigDecimal, 1),
	new ProductItem("id2", "商品2", BigDecimal("3000").bigDecimal, 2),
	new ProductItem("id3", "商品3", BigDecimal("1500").bigDecimal, 3)
)
// (1) マッピング
val names = items.map { _.getName() }

names.foreach { println }

println("-----")

// (2) フィルタリング
val highItems = items.filter { _.getPrice() >= 1500 }

highItems.foreach { it => println(it.getName()) }

println("-----")

// (3) 畳み込み
def total = items.foldLeft(0: BigDecimal) {(a, b) =>
	a + b.getPrice() * b.getQty()
}

println(total)
