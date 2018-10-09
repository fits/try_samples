
sealed abstract case class Quantity private (amount: Int)

object Quantity {
  def create(amount: Int): Quantity = new Quantity(amount){}
}

println(Quantity.create(1))

// error: value copy is not a member of this.Quantity
//println(Quantity.create(1).copy(2))
