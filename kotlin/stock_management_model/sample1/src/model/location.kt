package model

typealias LocationId = String

interface Location {
    val id: LocationId
    val name: String
}

interface RealLocation : Location {}

data class InventoryLocation(
    override val id: LocationId,
    override val name: String = ""
) : RealLocation
