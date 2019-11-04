package model

interface StockMove {
    val itemId: ItemId
    val qty: Quantity
}

typealias StockId = Pair<ItemId, LocationId>

fun stockId(itemId: ItemId, location: Location) = Pair(itemId, location.id)

typealias RealAssign = Assign<RealLocation>

typealias LocationFinder = (LocationId) -> Location?
typealias RealStockFinder = (StockId) -> RealStock?

typealias CreateStockMove = (ItemId, Quantity, LocationId, LocationId) -> UnvalidateStockMove?
typealias ValidateStockMove = (LocationFinder, UnvalidateStockMove) -> ValidatedStockMove?
typealias AssignStockMove = (RealStockFinder, ValidatedStockMove) -> Pair<AssignedStockMove, RealStock>?
typealias CompleteStockMove = (RealStockFinder, AssignedStockMove) ->
                                Triple<CompletedStockMove, RealStock, RealStock>?

object StockMoveOp {
    val create: CreateStockMove = { item, qty, from, to ->
        if (qty >= 0) UnvalidateStockMove(item, qty, from, to) else null
    }

    val validate: ValidateStockMove = { lfinder, mv ->
        lfinder(mv.from)?.let { f ->
            lfinder(mv.to)?.let { t ->
                when(t) {
                    is RealLocation -> ValidatedStockMove(mv.itemId, mv.qty, f, t)
                    else -> null
                }
            }
        }
    }

    val assign: AssignStockMove = { sfinder, mv ->
        sfinder(stockId(mv.itemId, mv.from))?.let { s ->
            s.assign(mv.qty)?.let { (ns, asn) ->
                Pair(
                    AssignedStockMove(mv.itemId, mv.qty, mv.from, mv.to, asn),
                    ns
                )
            }
        }
    }

    val complete: CompleteStockMove = { sfinder, mv ->
        sfinder(stockId(mv.itemId, mv.assign.location))?.let { stockFrom ->
            sfinder(stockId(mv.itemId, mv.to))?.let { stockTo ->
                stockTo.stock(mv.assign.qty)?.let { newStockTo ->
                    stockFrom.completeAssign(mv.assign)?.let { newStockFrom ->
                        Triple(
                            CompletedStockMove(mv.itemId, mv.qty, mv.from, mv.to),
                            newStockFrom,
                            newStockTo
                        )
                    }
                }
            }
        }
    }
}

data class UnvalidateStockMove(
    override val itemId: ItemId,
    override val qty: Quantity,
    val from: LocationId,
    val to: LocationId
) : StockMove

data class ValidatedStockMove(
    override val itemId: ItemId,
    override val qty: Quantity,
    val from: Location,
    val to: RealLocation
) : StockMove

data class AssignedStockMove(
    override val itemId: ItemId,
    override val qty: Quantity,
    val from: Location,
    val to: RealLocation,
    val assign: RealAssign
) : StockMove

data class CompletedStockMove(
    override val itemId: ItemId,
    override val qty: Quantity,
    val from: Location,
    val to: RealLocation
) : StockMove