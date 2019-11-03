package model

typealias ItemCode = String
typealias Quantity = Int

interface Stock {
    val item: ItemCode
    val qty: Quantity
    fun stocked(q: Quantity): Stock?

    companion object {
        fun create(item: ItemCode, qty: Quantity): Stock? =
            if (qty >= 0) StockData(item, qty) else null
    }
}

private data class StockData(
    override val item: ItemCode,
    override val qty: Quantity = 0
) : Stock {
    override fun stocked(q: Quantity): Stock? =
        if (q > 0) copy(qty = qty + q) else null
}