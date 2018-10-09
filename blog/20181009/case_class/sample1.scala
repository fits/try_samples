
case class Quantity private (amount: Int)

//println(new Quantity(1))
println(Quantity(1))
println(Quantity.apply(2))
println(Quantity(3).copy(30))
