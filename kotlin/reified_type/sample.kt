
typealias LocationId = String

interface Location {
    val id: LocationId
}

data class WarehouseLocation(override val id: LocationId) : Location
data class StoreLocation(override val id: LocationId) : Location

inline fun <reified L : Location> selectLocations(m: Map<LocationId, Location>): Map<LocationId, L> =
    m.values.flatMap {
        if (it is L) listOf(it) else listOf()
    }.map {
        Pair(it.id, it)
    }.toMap()

fun main() {
    val w1 = WarehouseLocation("warehouse1")
    val w2 = WarehouseLocation("warehouse2")
    val s1 = StoreLocation("store1")

    val m = listOf(w1, w2, s1).map { Pair(it.id, it) }.toMap()

    // Map<LocationId, WarehouseLocation>
    println( selectLocations<WarehouseLocation>(m) )

    // Map<LocationId, StoreLocation>
    println( selectLocations<StoreLocation>(m) )
}