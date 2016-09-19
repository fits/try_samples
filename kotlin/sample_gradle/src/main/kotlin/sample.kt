
data class Product(val productId: String, val name: String)

fun main(args: Array<String>) {

	println("sample")

	val p1 = Product("p1", "aaa")

	println(p1)
	println(p1.copy())
	println(p1.copy(name = "abc"))

	val (id, name) = p1

	println("id: ${id}, name: ${name}")

}