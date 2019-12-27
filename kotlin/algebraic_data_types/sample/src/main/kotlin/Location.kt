
typealias LocationCode = String
typealias Address = String

interface LocationInfo {
    val code: LocationCode
    val name: String
}

sealed class Location : LocationInfo {
    data class RealLocation(override val code: LocationCode, override val name: String,
                            val address: Address = "") : Location()

    data class VirtualLocation(override val code: LocationCode, override val name: String,
                               val locations: List<RealLocation>) : Location()
}