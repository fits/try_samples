
import model.*

fun main() {
    val item1 = "item1"
    val inv1 = InventoryLocation("inv1", "warehouse-1")
    val inv2 = InventoryLocation("inv2", "warehouse-2")

    val st1 = RealStock.create(item1, inv1, 10)!!
    val st2 = RealStock.create(item1, inv2, 0)!!

    val locationMap = listOf(inv1, inv2).map { Pair(it.id, it) }.toMap()

    var stockMap = listOf(st1, st2).map {
        Pair(stockId(it.itemId, it.location), it)
    }.toMap()

    val updateStock = { st: RealStock ->
        stockMap = stockMap.plus(Pair(stockId(st.itemId, st.location), st))
    }

    StockMoveOp.create(item1, 2, inv1.id, inv2.id)?.let { mv1 ->
        println(mv1)

        StockMoveOp.validate(locationMap::get, mv1)?.let { mv2 ->
            println(mv2)

            StockMoveOp.assign(stockMap::get, mv2)?.let { (mv3, st) ->
                println(mv3)
                println(st)

                updateStock(st)

                StockMoveOp.complete(stockMap::get, mv3)?.let { (mv4, st1, st2) ->
                    listOf(st1, st2).forEach(updateStock)

                    println(mv4)
                    println(listOf(st1, st2))
                }
            }
        }
    }
}