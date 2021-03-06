package model

typealias ItemId = String
typealias Quantity = Int

data class Assign<L : Location>(
    val itemId: ItemId,
    val location: L,
    val qty: Quantity
)

interface Stock<L : Location> {
    val itemId: ItemId
    val location: L
    val qty: Quantity
}

interface StockOp<L : Location, S : StockOp<L, S>> {
    fun inStore(q: Quantity): S?
    fun outStore(q: Quantity): S?
    fun outStoreBy(a: Assign<L>): S?
}

interface AssignOp<L : Location, S : AssignOp<L, S>> {
    fun assign(q: Quantity): Pair<S, Assign<L>>?
    fun cancelAssign(a: Assign<L>): S?
}

typealias RealStockOp<S> = StockOp<RealLocation, S>
typealias RealAssignOp<S> = AssignOp<RealLocation, S>

interface RealStock : Stock<RealLocation>, RealStockOp<RealStock>, RealAssignOp<RealStock> {
    val realQty: Quantity
    val assignedQty: Quantity

    companion object {
        fun create(itemId: ItemId, location: RealLocation, realQty: Quantity): RealStock? =
            if (realQty >= 0)
                RealStockData(itemId, location, realQty)
            else null
    }
}

private data class RealStockData(
    override val itemId: ItemId,
    override val location: RealLocation,
    override val realQty: Quantity = 0,
    override val assignedQty: Quantity = 0,
    override val qty: Quantity = realQty - assignedQty
) : RealStock {
    override fun inStore(q: Quantity): RealStock? =
        if (q > 0 && this.realQty + q >= 0)
            copy(qty = this.qty + q, realQty = this.realQty + q)
        else null

    override fun outStore(q: Quantity): RealStock? =
        if (q > 0 && this.realQty - q >= 0)
            copy(qty = this.qty - q, realQty = this.realQty - q)
        else null

    override fun outStoreBy(a: Assign<RealLocation>): RealStock? =
        if (validateAssign((a)))
            copy(realQty = this.realQty - a.qty, assignedQty =  this.assignedQty - a.qty)
        else null

    override fun assign(q: Quantity): Pair<RealStock, Assign<RealLocation>>? =
        if (q > 0 && this.qty - q >= 0)
            Pair(copy(qty = this.qty - q, assignedQty = this.assignedQty + q), createAssign(q))
        else null

    override fun cancelAssign(a: Assign<RealLocation>): RealStock? =
        if (validateAssign(a))
            copy(qty = this.qty + a.qty, assignedQty = this.assignedQty - a.qty)
        else null

    private fun createAssign(q: Quantity) = Assign(itemId, location, q)
    private fun validateAssign(a: Assign<RealLocation>) =
        this.itemId == a.itemId && this.location == a.location
}