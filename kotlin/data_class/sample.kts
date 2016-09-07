
data class Product(val productId: String, val name: String)


println("sample")

val p1 = Product("p1", "aaa")

println(p1)
println(p1.copy())
println(p1.copy(name = "abc"))
