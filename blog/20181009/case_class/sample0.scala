
case class Quantity(amount: Int) {
  if (amount < 0)
    throw new IllegalArgumentException(s"amount($amount) < 0")
}

println(Quantity(1))
println(Quantity(0))
println(Quantity(-1))
