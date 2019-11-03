
import model.*

fun main() {
    Stock.create("item1", 2)?.let {
        val res = it.stocked(8)
        println(res)
    }
}