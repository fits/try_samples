
import scala.util.{Try, Success, Failure}

sealed abstract case class Quantity private (amount: Int)

object Quantity {
  def apply(amount: Int): Try[Quantity] =
    if (amount >= 0)
      Success(new Quantity(amount){})
    else
      Failure(new IllegalArgumentException(s"amount($amount) < 0"))
}

println(Quantity(1))
println(Quantity(0))
println(Quantity(-1))

//  error: value copy is not a member of this.Quantity
//println(Quantity(1).map(_.copy(-1)))
