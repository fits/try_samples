
typealias Quantity = Int

interface Stock<T : Stock<T>> {
    val qty: Quantity
    fun update(newQty: Quantity): T
}

data class BasicStock(override val qty: Quantity = 0) : Stock<BasicStock> {
    override fun update(newQty: Quantity): BasicStock = copy(qty = newQty)
}

data class FixedStock(override val qty: Quantity = 0) : Stock<FixedStock> {
    override fun update(newQty: Quantity): FixedStock = this
}

fun main() {
    val s1 = BasicStock(1)

    println(s1)
    println(s1.update(5))

    val s2 = FixedStock(2)

    println(s2)
    println(s2.update(6))
}