
import com.google.gson.Gson

typealias DataId = String
typealias DataValue = Int

data class Data(val id: DataId, val items: List<DataItem>)
data class DataItem(val value: DataValue)

fun main() {
    val gson = Gson()

    val d = Data("data1", listOf(DataItem(1), DataItem(2)))
    println(d)

    val str = gson.toJson(d)
    println(str)

    val d2 = gson.fromJson(str, Data::class.java)
    println(d2)

    assert(d == d2)
}