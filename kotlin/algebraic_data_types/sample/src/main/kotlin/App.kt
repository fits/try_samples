
import Location.*

fun main() {
    val rl1 = RealLocation("rl-1", "sample1")
    val rl2 = RealLocation("rl-2", "sample2", "xxxx")

    println(rl1)

    val vl1 = VirtualLocation("vl-1", "sample3", listOf(rl1, rl2))

    println(vl1)

    printInfo(rl2)
    printInfo(vl1)

    printLocation(rl1)
    printLocation(rl2)

    printLocation(vl1)
}

fun printInfo(info: LocationInfo) {
    println("code: ${info.code}, name: ${info.name}")
}

fun printLocation(loc: Location) {
    when (loc) {
        is RealLocation -> println("name: ${loc.name}, address: ${loc.address}")
        is VirtualLocation -> println("name: ${loc.name}, locations: ${loc.locations}")
    }
}